package io.github.apace100.origins.util.codec;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class RecipeCodec implements Codec<Recipe<?>> {
	private static final Map<Class<? extends Recipe<?>>, Function<? extends Recipe<?>, RecipeJsonProvider>> SERIALIZABLE_TYPES = new HashMap<>();
	public static Codec<Recipe<?>> CODEC = new RecipeCodec();

	static {
		add(ShapedRecipe.class, RecipeCodec::convertShaped);
		add(ShapelessRecipe.class, RecipeCodec::convertShapeless);
	}

	public static <T extends Recipe<?>> void add(Class<T> cls, Function<T, RecipeJsonProvider> serializer) {
		SERIALIZABLE_TYPES.put(cls, serializer);
	}

	@Nullable
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static RecipeJsonProvider convert(Recipe<?> recipe) {
		for (Map.Entry<Class<? extends Recipe<?>>, Function<? extends Recipe<?>, RecipeJsonProvider>> entry : SERIALIZABLE_TYPES.entrySet()) {
			if (entry.getKey().isInstance(recipe))
				return (RecipeJsonProvider) ((Function) entry.getValue()).apply(recipe);
		}
		return null;
	}

	private static RecipeJsonProvider convertShaped(ShapedRecipe recipe) {
		ItemStack output = recipe.getOutput().copy();
		DefaultedList<Ingredient> inputs = recipe.getPreviewInputs();
		int width = recipe.getWidth();
		char[][] pattern = new char[recipe.getHeight()][width];
		AtomicInteger key = new AtomicInteger('A');
		Map<Ingredient, Character> map = new HashMap<>();
		synchronized (key) {
			for (int i = 0; i < inputs.size(); i++) {
				if (inputs.get(i).isEmpty())
					pattern[i / width][i % width] = ' ';
				else
					pattern[i / width][i % width] = map.computeIfAbsent(inputs.get(i), ingredient -> (char) key.getAndAdd(1));
			}
		}
		ShapedRecipeJsonFactory factory = ShapedRecipeJsonFactory.create(output.getItem(), output.getCount()).group(recipe.getGroup());
		for (char[] chars : pattern) factory.pattern(new String(chars));
		map.forEach((ingredient, character) -> factory.input(character, ingredient));
		AtomicReference<RecipeJsonProvider> reference = new AtomicReference<>(null);
		factory.offerTo(reference::set);
		return reference.get();
	}

	private static RecipeJsonProvider convertShapeless(ShapelessRecipe recipe) {
		ItemStack output = recipe.getOutput().copy();
		DefaultedList<Ingredient> inputs = recipe.getPreviewInputs();
		ShapelessRecipeJsonFactory factory = ShapelessRecipeJsonFactory.create(output.getItem(), output.getCount()).group(recipe.getGroup());
		inputs.forEach(factory::input);
		AtomicReference<RecipeJsonProvider> reference = new AtomicReference<>(null);
		factory.offerTo(reference::set);
		return reference.get();
	}

	private static DataResult<RecipeSerializer<?>> findSerializer(String type) {
		Identifier identifier = Identifier.tryParse(type);
		if (identifier == null)
			return DataResult.error("Malformed resource: " + type);
		return Registry.RECIPE_SERIALIZER.getOrEmpty(identifier).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown recipe serializer: " + type)).map(Function.identity());
	}

	private boolean isDataContext(DynamicOps<?> ops) {
		return ops instanceof JsonOps && !ops.compressMaps();
	}

	@Override
	public <T> DataResult<Pair<Recipe<?>, T>> decode(DynamicOps<T> ops, T input) {
		if (this.isDataContext(ops)) {
			return ops.getMap(input)
					.flatMap(map -> ops.getStringValue(map.get("type"))
							.flatMap(RecipeCodec::findSerializer)
							.flatMap(serializer -> ops.getStringValue(map.get(input)).flatMap(id -> {
								Identifier identifier = Identifier.tryParse(id);
								if (identifier == null)
									return DataResult.error("Malformed id: " + id);
								try {
									return DataResult.success(new Pair<>(serializer.read(identifier, (JsonObject) ops.convertTo(JsonOps.INSTANCE, input)), ops.empty()));
								} catch (JsonParseException exception) {
									return DataResult.error("Recipe failed with error: " + exception.getMessage());
								}
							})));
		}
		return ops.getByteBuffer(input).flatMap(buffer -> {
			PacketByteBuf buf = new PacketByteBuf(Unpooled.copiedBuffer(buffer));
			try {
				Identifier identifier = buf.readIdentifier();
				Optional<RecipeSerializer<?>> serializer = Registry.RECIPE_SERIALIZER.getOrEmpty(identifier);
				if (serializer.isEmpty())
					return DataResult.error("Unknown recipe serializer: " + identifier);
				identifier = buf.readIdentifier();
				return DataResult.success(new Pair<>(serializer.get().read(identifier, buf), ops.empty()));
			} catch (Exception e) {
				return DataResult.error("Failed to read buffer: " + e.getMessage());
			} finally {
				buf.release();
			}
		});
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public <T> DataResult<T> encode(Recipe<?> input, DynamicOps<T> ops, T prefix) {
		Identifier serializerId = Registry.RECIPE_SERIALIZER.getId(input.getSerializer());
		if (serializerId == null)
			return DataResult.error("Couldn't find id for serializer: " + input.getSerializer());
		if (this.isDataContext(ops)) {
			RecipeJsonProvider convert = convert(input);
			if (convert == null)
				return DataResult.error("No serialization method were found for type: " + input.getClass());
			JsonObject object = convert.toJson();
			object.addProperty("id", input.getId().toString());
			return DataResult.success(JsonOps.INSTANCE.convertTo(ops, object));
		}
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		try {
			buf.writeIdentifier(serializerId);
			buf.writeIdentifier(input.getId());
			((Recipe) input).getSerializer().write(buf, input);
			ByteBuffer buffer = buf.nioBuffer();
			return DataResult.success(ops.createByteList(buffer));
		} catch (Exception e) {
			return DataResult.error("Failed to write buffer: " + e);
		} finally {
			buf.release();
		}
	}
}
