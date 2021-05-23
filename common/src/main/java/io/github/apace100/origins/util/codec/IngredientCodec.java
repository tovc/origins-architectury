package io.github.apace100.origins.util.codec;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;

import java.nio.ByteBuffer;
import java.util.Objects;

public class IngredientCodec implements Codec<Ingredient> {
	@Override
	public <T> DataResult<Pair<Ingredient, T>> decode(DynamicOps<T> ops, T input) {
		if (!ops.compressMaps()) {
			try {
				return DataResult.success(Pair.of(Ingredient.fromJson(ops.convertTo(JsonOps.INSTANCE, input)), ops.empty()));
			} catch (JsonParseException e) {
				return DataResult.error("Failed to decode Ingredient: " + e.getMessage());
			}
		}
		return ops.getByteBuffer(input).map(x -> {
			PacketByteBuf buf = new PacketByteBuf(Unpooled.copiedBuffer(x));
			Ingredient ingredient = Ingredient.fromPacket(buf);
			buf.release();
			return Pair.of(ingredient, ops.empty());
		});
	}

	@Override
	public <T> DataResult<T> encode(Ingredient input, DynamicOps<T> ops, T prefix) {
		if (!Objects.equals(prefix, ops.empty()))
			return DataResult.error("Cannot encode ingredients with prefixes");
		if (ops.compressMaps()) {
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			input.write(buf);
			ByteBuffer byteBuffer = buf.nioBuffer();
			T out = ops.createByteList(byteBuffer);
			buf.release();
			return DataResult.success(out);
		}
		return DataResult.success(JsonOps.INSTANCE.convertTo(ops, input.toJson()));
	}
}
