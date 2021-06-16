package io.github.apace100.origins.util;

import com.google.common.collect.*;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.origin.Impact;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.PowerTypeReference;
import io.github.apace100.origins.power.PowerTypes;
import io.github.apace100.origins.util.codec.IngredientCodec;
import io.github.apace100.origins.util.codec.InlineCodec;
import io.github.apace100.origins.util.codec.OptionalField;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.*;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OriginsCodecs {
	//Large Codecs
	public static final Codec<Identifier> FUZZY_IDENTIFIER = Codec.STRING.flatXmap(input -> {
		String namespace = "minecraft";
		String path;
		if (input.contains(":")) {
			String[] split = input.split(":");
			if (split.length != 2)
				return DataResult.error("Incorrect number of `:` in identifier: \"" + input + "\".");
			namespace = split[0];
			path = split[1];
		} else
			path = input;
		if (namespace.contains("*")) {
			if (PowerTypes.CURRENT_NAMESPACE != null)
				namespace = namespace.replace("*", PowerTypes.CURRENT_NAMESPACE);
			else
				return DataResult.error("Identifier may only contain a `*` in the namespace inside of powers.");
		}
		if (path.contains("*")) {
			if (PowerTypes.CURRENT_PATH != null)
				path = path.replace("*", PowerTypes.CURRENT_NAMESPACE);
			else
				return DataResult.error("Identifier may only contain a `*` in the path inside of powers.");
		}
		try {
			return DataResult.success(new Identifier(namespace + ":" + path));
		} catch (InvalidIdentifierException var2) {
			return DataResult.error("Not a valid resource location: " + namespace + ":" + path + " " + var2.getMessage());
		}
	}, x -> DataResult.success(x.toString()));

	public static final Codec<OptionalField> REGISTRY_ENTRY = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("name").forGetter(x -> x.value),
			Codec.BOOL.optionalFieldOf("optional", false).forGetter(x -> x.optional)
	).apply(instance, OptionalField::new));

	public static final Codec<DamageSource> DAMAGE_SOURCE = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(DamageSource::getName),
			Codec.BOOL.optionalFieldOf("bypasses_armor", false).forGetter(DamageSource::bypassesArmor),
			Codec.BOOL.optionalFieldOf("fire", false).forGetter(DamageSource::isFire),
			Codec.BOOL.optionalFieldOf("unblockable", false).forGetter(DamageSource::isUnblockable),
			Codec.BOOL.optionalFieldOf("magic", false).forGetter(DamageSource::getMagic),
			Codec.BOOL.optionalFieldOf("out_of_world", false).forGetter(DamageSource::isOutOfWorld)
	).apply(instance, (name, bypassesArmor, fire, unblockable, magic, outOfWorld) -> {
		DamageSource ds = new DamageSource(name);
		if (bypassesArmor) ds.setBypassesArmor();
		if (fire) ds.setFire();
		if (unblockable) ds.setUnblockable();
		if (magic) ds.setUsesMagic();
		if (outOfWorld) ds.setOutOfWorld();
		return ds;
	}));

	public static final Codec<StatusEffectInstance> STATUS_EFFECT_INSTANCE = RecordCodecBuilder.create(instance -> instance.group(
			Registry.STATUS_EFFECT.fieldOf("effect").forGetter(StatusEffectInstance::getEffectType),
			Codec.INT.optionalFieldOf("duration", 100).forGetter(StatusEffectInstance::getDuration),
			Codec.INT.optionalFieldOf("amplifier", 0).forGetter(StatusEffectInstance::getAmplifier),
			Codec.BOOL.optionalFieldOf("is_ambient", false).forGetter(StatusEffectInstance::isAmbient),
			Codec.BOOL.optionalFieldOf("show_particles", false).forGetter(StatusEffectInstance::shouldShowParticles),
			Codec.BOOL.optionalFieldOf("show_icon", false).forGetter(StatusEffectInstance::shouldShowIcon)
	).apply(instance, StatusEffectInstance::new));

	//Enum Codec
	public static final Codec<Comparison> COMPARISON = enumCodec(Comparison.values(), Arrays.stream(Comparison.values()).collect(ImmutableMap.toImmutableMap(Comparison::getComparisonString, Function.identity())));
	public static final Codec<LightType> LIGHT_TYPE = enumCodec(LightType.values(), ImmutableMap.of());
	public static final Codec<GameMode> GAME_MODE = enumCodec(GameMode.values(), Arrays.stream(GameMode.values()).collect(Collectors.toMap(GameMode::getName, Function.identity())));
	public static final Codec<EquipmentSlot> EQUIPMENT_SLOT = enumCodec(EquipmentSlot.values(), ImmutableMap.of());
	public static final Codec<EntityAttributeModifier.Operation> MODIFIER_OPERATION = enumCodec(EntityAttributeModifier.Operation.values(), ImmutableMap.of());
	public static final Codec<Shape> SHAPE = enumCodec(Shape.values(), ImmutableMap.of());
	public static final Codec<Space> SPACE = enumCodec(Space.values(), ImmutableMap.of());
	public static final Codec<Impact> IMPACT = enumCodec(Impact.values(), ImmutableMap.of());

	public static final MapCodec<EntityAttributeModifier> ENTITY_ATTRIBUTE_MODIFIER_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.optionalFieldOf("name", "Unnamed attribute modifier").forGetter(EntityAttributeModifier::getName),
			Codec.DOUBLE.fieldOf("operation").forGetter(EntityAttributeModifier::getValue),
			MODIFIER_OPERATION.fieldOf("operation").forGetter(EntityAttributeModifier::getOperation)
	).apply(instance, EntityAttributeModifier::new));

	//Tag Codecs
	public static final Codec<Tag<Block>> BLOCK_TAG = Tag.codec(() -> ServerTagManagerHolder.getTagManager().getBlocks());
	public static final Codec<Tag<Item>> ITEM_TAG = Tag.codec(() -> ServerTagManagerHolder.getTagManager().getItems());
	public static final Codec<Tag<Fluid>> FLUID_TAG = Tag.codec(() -> ServerTagManagerHolder.getTagManager().getFluids());
	public static final Codec<Tag<EntityType<?>>> ENTITY_TAG = Tag.codec(() -> ServerTagManagerHolder.getTagManager().getEntityTypes());
	//Simple Codecs
	public static final Codec<PowerType<?>> POWER_TYPE = FUZZY_IDENTIFIER.xmap(PowerTypeReference::new, PowerType::getIdentifier);
	public static final Codec<RegistryKey<World>> DIMENSION = Identifier.CODEC.xmap(x -> RegistryKey.of(Registry.DIMENSION, x), RegistryKey::getValue);
	public static final Codec<EntityGroup> ENTITY_GROUP = mappedCodec(Codec.STRING, ImmutableBiMap.of("default", EntityGroup.DEFAULT, "undead", EntityGroup.UNDEAD, "arthropod", EntityGroup.ARTHROPOD, "illager", EntityGroup.ILLAGER, "aquatic", EntityGroup.AQUATIC));
	public static final Codec<Ingredient> INGREDIENT = new IngredientCodec();
	//Registry Codecs
	public static final Codec<Optional<EntityAttribute>> OPTIONAL_ATTRIBUTE = optionalRegistry(Registry.ATTRIBUTE::getOrEmpty, Registry.ATTRIBUTE::getId);
	public static final Codec<Optional<Biome>> OPTIONAL_BIOME = optionalRegistry(BuiltinRegistries.BIOME::getOrEmpty, BuiltinRegistries.BIOME::getId);
	public static final Codec<Optional<Block>> OPTIONAL_BLOCK = optionalRegistry(Registry.BLOCK::getOrEmpty, Registry.BLOCK::getId);
	public static final Codec<Optional<EntityType<?>>> OPTIONAL_ENTITY_TYPE = optionalRegistry(Registry.ENTITY_TYPE::getOrEmpty, Registry.ENTITY_TYPE::getId);
	public static final Codec<Optional<Enchantment>> OPTIONAL_ENCHANTMENT = optionalRegistry(Registry.ENCHANTMENT::getOrEmpty, Registry.ENCHANTMENT::getId);
	public static final Codec<Optional<Item>> OPTIONAL_ITEM = optionalRegistry(Registry.ITEM::getOrEmpty, Registry.ITEM::getId);
	public static final Codec<Optional<StatusEffect>> OPTIONAL_STATUS_EFFECT = optionalRegistry(Registry.STATUS_EFFECT::getOrEmpty, Registry.STATUS_EFFECT::getId);
	public static final Codec<Optional<SoundEvent>> OPTIONAL_SOUND_EVENT = optionalRegistry(Registry.SOUND_EVENT::getOrEmpty, Registry.SOUND_EVENT::getId);

	public static final Codec<Optional<AttributedEntityAttributeModifier>> OPTIONAL_ATTRIBUTED_ATTRIBUTE_MODIFIER = RecordCodecBuilder.create(instance -> instance.group(
			OPTIONAL_ATTRIBUTE.fieldOf("attribute").forGetter(x -> x.map(AttributedEntityAttributeModifier::getAttribute)),
			ENTITY_ATTRIBUTE_MODIFIER_MAP_CODEC.forGetter(x -> x.map(AttributedEntityAttributeModifier::getModifier).get())
	).apply(instance, (entityAttribute, entityAttributeModifier) -> entityAttribute.map(x -> new AttributedEntityAttributeModifier(x, entityAttributeModifier))));

	public static final Codec<Optional<ItemStack>> ITEM_STACK = RecordCodecBuilder.create(instance -> instance.group(
			OPTIONAL_ITEM.fieldOf("item").forGetter(x -> x.map(ItemStack::getItem)),
			Codec.INT.optionalFieldOf("amount", 1).forGetter(x -> x.map(ItemStack::getCount).orElse(1)),
			CompoundTag.CODEC.optionalFieldOf("tag").forGetter(x -> x.map(ItemStack::getTag))
	).apply(instance, (item, integer, compoundTag) -> item.map(i -> {
		ItemStack is = new ItemStack(i, integer);
		compoundTag.ifPresent(is::setTag);
		return is;
	})));

	public static final Codec<ItemStack> ITEM_OR_ITEM_STACK = Codec.either(OPTIONAL_ITEM.xmap(x -> x.map(ItemStack::new), x -> x.map(ItemStack::getItem)), ITEM_STACK)
			.xmap(x -> x.map(Function.identity(), Function.identity()).orElse(ItemStack.EMPTY), x -> Either.right(x.isEmpty() ? Optional.empty() : Optional.of(x)));

	public static <T extends Enum<T>> Codec<T> enumCodec(T[] values, Map<String, T> additional) {
		Map<String, T> builder = new HashMap<>(additional);
		for (T value : values) {
			String key = value.name().toLowerCase(Locale.ROOT);
			builder.put(key, value);
			if (value instanceof StringIdentifiable) {
				String name = ((StringIdentifiable) value).asString().toLowerCase(Locale.ROOT);
				if (!Objects.equals(name, key))
					builder.put(name, value);
			}
		}
		ImmutableMap<String, T> map = ImmutableMap.copyOf(builder);
		return Codec.either(
				Codec.intRange(0, values.length - 1).xmap(i -> values[i], Enum::ordinal),
				Codec.STRING.flatXmap(
						x -> map.containsKey(x.toLowerCase(Locale.ROOT)) ? DataResult.success(map.get(x.toLowerCase(Locale.ROOT))) : DataResult.<T>error("Couldn't find field " + x + " for enum " + values.getClass().getComponentType()),
						x -> DataResult.success(x.name().toLowerCase(Locale.ROOT)))
		).xmap(x -> x.map(Function.identity(), Function.identity()), Either::left);
	}

	/**
	 * This codec is equivalent to {@link Codec#list(Codec)} with the exception that
	 * if the object is a single value instead of a {@link List}, it will be correctly handled and
	 * converted to a list.
	 *
	 * @param source The codec to make a list out of.
	 *
	 * @return The list codec.
	 */
	public static <T> Codec<List<T>> listOf(Codec<T> source) {
		return Codec.either(source, source.listOf()).xmap(x -> x.map(Lists::newArrayList, Function.identity()), Either::right);
	}

	public static <T> Codec<Set<T>> setOf(Codec<T> source) {
		return Codec.either(source, source.listOf()).xmap(x -> x.map(Sets::newHashSet, HashSet::new), x -> Either.right(new ArrayList<>(x)));
	}

	/**
	 * Represents a registry entry, stored and read via {@link Identifier}, with platform and optional checking.
	 *
	 * @param accessor   The function used to access the object from it's {@link Identifier}
	 * @param nameGetter The function used to access the {@link Identifier} from the object.
	 *
	 * @return A new codec that supports inline and object type with fields "type", "optional" and "platform".
	 */
	public static <T> Codec<Optional<T>> optionalRegistry(Function<Identifier, Optional<T>> accessor, Function<T, Identifier> nameGetter) {
		return Codec.either(Identifier.CODEC.flatXmap(x -> DataResult.success(accessor.apply(x)), x -> x.map(nameGetter).map(DataResult::success).orElseGet(() -> DataResult.error("Object " + x + " wan't registered"))), REGISTRY_ENTRY)
				.flatXmap(x -> x.map(DataResult::success, t -> t.get(accessor)), t -> DataResult.success(Either.left(t)));
	}

	/**
	 * A dispatch codec whose second part will be inlined if no maps aren't being compressed.
	 *
	 * @param typeKey       The name of the master type.
	 * @param sourceCodec   The codec for the factory type.
	 * @param accessor      A way to access the factory from the instance.
	 * @param codecAccessor A way to access the codec of the instance from the master.
	 *
	 * @return The new codec.
	 */
	public static <E, A> Codec<E> inlineDispatch(String typeKey, Codec<A> sourceCodec, final Function<? super E, ? extends A> accessor, final Function<? super A, ? extends Codec<? extends E>> codecAccessor) {
		return new InlineCodec<>(typeKey, sourceCodec, accessor, codecAccessor);
	}

	public static <E, A> Codec<E> inlineDispatch(Codec<A> sourceCodec, final Function<? super E, ? extends A> accessor, final Function<? super A, ? extends Codec<? extends E>> codecAccessor) {
		return inlineDispatch("type", sourceCodec, accessor, codecAccessor);
	}

	public static <E, A> Codec<E> mappedCodec(Codec<A> keyCodec, BiMap<A, E> map) {
		return keyCodec.flatXmap(x -> !map.containsKey(x) ? DataResult.error("Unknown key: " + x) : DataResult.success(map.get(x)), x -> !map.inverse().containsKey(x) ? DataResult.error("Unknown value: " + x) : DataResult.success(map.inverse().get(x)));
	}

	public static <E> Codec<FilterableWeightedList<E>> weightedListOf(Codec<E> source) {
		return RecordCodecBuilder.<Pair<E, Integer>>create(instance -> instance.group(
				source.fieldOf("element").forGetter(Pair::getLeft),
				Codec.INT.fieldOf("weight").forGetter(Pair::getRight)
		).apply(instance, Pair::new)).listOf().xmap(x -> {
			FilterableWeightedList<E> ls = new FilterableWeightedList<>();
			x.forEach(y -> ls.add(y.getLeft(), y.getRight()));
			return ls;
		}, x -> x.entryStream().map(y -> new Pair<>(y.getElement(), y.weight)).collect(Collectors.toList()));
	}
}
