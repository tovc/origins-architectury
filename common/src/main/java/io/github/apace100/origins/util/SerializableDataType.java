package io.github.apace100.origins.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.origin.Impact;
import io.github.apace100.origins.origin.OriginUpgrade;
import io.github.apace100.origins.power.Active;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.PowerTypeReference;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.power.factory.action.ActionType;
import io.github.apace100.origins.power.factory.action.ActionTypes;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.power.factory.condition.ConditionType;
import io.github.apace100.origins.power.factory.condition.ConditionTypes;
import io.github.apace100.origins.util.codec.InlineJsonOps;
import me.shedaniel.architectury.hooks.TagHooks;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * When this mod will update to 1.17, this class will go away and be replaced by
 * {@link com.mojang.serialization.Codec Codecs}. This will allow me to rewrite network
 * completely to work with forge.
 */
@Deprecated
@ApiStatus.ScheduledForRemoval(inVersion = "MC 1.17")
public class SerializableDataType<T> {

	public static final SerializableDataType<Integer> INT = new SerializableDataType<>(Integer.class, Codec.INT);

	public static final SerializableDataType<Boolean> BOOLEAN = new SerializableDataType<>(Boolean.class, Codec.BOOL);

	public static final SerializableDataType<Float> FLOAT = new SerializableDataType<>(Float.class, Codec.FLOAT);

	public static final SerializableDataType<Double> DOUBLE = new SerializableDataType<>(Double.class, Codec.DOUBLE);

	public static final SerializableDataType<String> STRING = new SerializableDataType<>(String.class, Codec.STRING);

	public static final SerializableDataType<Identifier> IDENTIFIER = new SerializableDataType<>(Identifier.class, OriginsCodecs.FUZZY_IDENTIFIER);

	public static final SerializableDataType<List<Identifier>> IDENTIFIERS = SerializableDataType.list(IDENTIFIER);

	public static final SerializableDataType<Impact> IMPACT = SerializableDataType.enumValue(Impact.class);

	public static final SerializableDataType<OriginUpgrade> UPGRADE = new SerializableDataType<>(OriginUpgrade.class, OriginUpgrade.CODEC);

	public static final SerializableDataType<List<OriginUpgrade>> UPGRADES = SerializableDataType.list(UPGRADE);

	public static final SerializableDataType<Enchantment> ENCHANTMENT = SerializableDataType.registry(Enchantment.class, Registry.ENCHANTMENT);

	public static final SerializableDataType<DamageSource> DAMAGE_SOURCE = SerializableDataType.compound(DamageSource.class, new SerializableData()
					.add("name", STRING)
					.add("bypasses_armor", BOOLEAN, false)
					.add("fire", BOOLEAN, false)
					.add("unblockable", BOOLEAN, false)
					.add("magic", BOOLEAN, false)
					.add("out_of_world", BOOLEAN, false),
			(data) -> {
				DamageSource damageSource = new DamageSource(data.get("name"));
				if (data.getBoolean("bypasses_armor")) {
					damageSource.setBypassesArmor();
				}
				if (data.getBoolean("fire")) {
					damageSource.setFire();
				}
				if (data.getBoolean("unblockable")) {
					damageSource.setUnblockable();
				}
				if (data.getBoolean("magic")) {
					damageSource.setUsesMagic();
				}
				if (data.getBoolean("out_of_world")) {
					damageSource.setOutOfWorld();
				}
				return damageSource;
			},
			(data, ds) -> {
				SerializableData.Instance inst = data.new Instance();
				inst.set("name", ds.name);
				inst.set("fire", ds.isFire());
				inst.set("unblockable", ds.isUnblockable());
				inst.set("bypasses_armor", ds.bypassesArmor());
				inst.set("out_of_world", ds.isOutOfWorld());
				inst.set("magic", ds.getMagic());
				return inst;
			});

	public static final SerializableDataType<EntityAttribute> ATTRIBUTE = SerializableDataType.registry(EntityAttribute.class, Registry.ATTRIBUTE);

	public static final SerializableDataType<EntityAttributeModifier> ATTRIBUTE_MODIFIER = new SerializableDataType<>(
			EntityAttributeModifier.class,
			SerializationHelper::writeAttributeModifier,
			SerializationHelper::readAttributeModifier,
			SerializationHelper::readAttributeModifier);

	public static final SerializableDataType<EntityAttributeModifier.Operation> MODIFIER_OPERATION = SerializableDataType.enumValue(EntityAttributeModifier.Operation.class);

	public static final SerializableDataType<AttributedEntityAttributeModifier> ATTRIBUTED_ATTRIBUTE_MODIFIER = SerializableDataType.compound(
			AttributedEntityAttributeModifier.class,
			new SerializableData()
					.add("attribute", ATTRIBUTE)
					.add("operation", MODIFIER_OPERATION)
					.add("value", DOUBLE)
					.add("name", STRING, "Unnamed EntityAttributeModifier"),
			dataInst -> {
				EntityAttribute attribute = dataInst.get("attribute");
				if (attribute == null)
					return null;
				return new AttributedEntityAttributeModifier(attribute, new EntityAttributeModifier(
						dataInst.get("name"),
						dataInst.getDouble("value"),
						dataInst.get("operation")));
			},
			(data, inst) -> {
				SerializableData.Instance dataInst = data.new Instance();
				dataInst.set("attribute", inst.getAttribute());
				dataInst.set("operation", inst.getModifier().getOperation());
				dataInst.set("value", inst.getModifier().getValue());
				dataInst.set("name", inst.getModifier().getName());
				return dataInst;
			});

	public static final SerializableDataType<List<EntityAttributeModifier>> ATTRIBUTE_MODIFIERS =
			SerializableDataType.list(ATTRIBUTE_MODIFIER);

	public static final SerializableDataType<List<AttributedEntityAttributeModifier>> ATTRIBUTED_ATTRIBUTE_MODIFIERS =
			SerializableDataType.list(ATTRIBUTED_ATTRIBUTE_MODIFIER);

	public static final SerializableDataType<PowerTypeReference> POWER_TYPE = SerializableDataType.wrap(
			PowerTypeReference.class, IDENTIFIER,
			PowerType::getIdentifier, PowerTypeReference::new);

	public static final SerializableDataType<Item> ITEM = SerializableDataType.registry(Item.class, Registry.ITEM);

	public static final SerializableDataType<StatusEffect> STATUS_EFFECT = SerializableDataType.registry(StatusEffect.class, Registry.STATUS_EFFECT);

	public static final SerializableDataType<List<StatusEffect>> STATUS_EFFECTS =
			SerializableDataType.list(STATUS_EFFECT);

	public static final SerializableDataType<StatusEffectInstance> STATUS_EFFECT_INSTANCE = new SerializableDataType<>(
			StatusEffectInstance.class,
			SerializationHelper::writeStatusEffect,
			SerializationHelper::readStatusEffect,
			SerializationHelper::readStatusEffect);

	public static final SerializableDataType<List<StatusEffectInstance>> STATUS_EFFECT_INSTANCES =
			SerializableDataType.list(STATUS_EFFECT_INSTANCE);

	public static final SerializableDataType<Tag<Fluid>> FLUID_TAG = SerializableDataType.wrap(ClassUtil.castClass(Tag.class), IDENTIFIER,
			fluid -> ServerTagManagerHolder.getTagManager().getFluids().getTagId(fluid),
			SerializationHelper::getFluidTagFromId);

	public static final SerializableDataType<Tag<Block>> BLOCK_TAG = SerializableDataType.wrap(ClassUtil.castClass(Tag.class), IDENTIFIER,
			block -> ServerTagManagerHolder.getTagManager().getBlocks().getTagId(block),
			SerializationHelper::getBlockTagFromId);

	public static final SerializableDataType<Comparison> COMPARISON = SerializableDataType.enumValue(Comparison.class,
			SerializationHelper.buildEnumMap(Comparison.class, Comparison::getComparisonString));

	public static final SerializableDataType<Space> SPACE = SerializableDataType.enumValue(Space.class);

	public static final SerializableDataType<ConditionFactory<LivingEntity>.Instance> ENTITY_CONDITION =
			SerializableDataType.condition(ClassUtil.castClass(ConditionFactory.Instance.class), ConditionTypes.ENTITY);

	public static final SerializableDataType<List<ConditionFactory<LivingEntity>.Instance>> ENTITY_CONDITIONS =
			SerializableDataType.list(ENTITY_CONDITION);

	public static final SerializableDataType<ConditionFactory<ItemStack>.Instance> ITEM_CONDITION =
			SerializableDataType.condition(ClassUtil.castClass(ConditionFactory.Instance.class), ConditionTypes.ITEM);

	public static final SerializableDataType<List<ConditionFactory<ItemStack>.Instance>> ITEM_CONDITIONS =
			SerializableDataType.list(ITEM_CONDITION);

	public static final SerializableDataType<ConditionFactory<CachedBlockPosition>.Instance> BLOCK_CONDITION =
			SerializableDataType.condition(ClassUtil.castClass(ConditionFactory.Instance.class), ConditionTypes.BLOCK);

	public static final SerializableDataType<List<ConditionFactory<CachedBlockPosition>.Instance>> BLOCK_CONDITIONS =
			SerializableDataType.list(BLOCK_CONDITION);

	public static final SerializableDataType<ConditionFactory<FluidState>.Instance> FLUID_CONDITION =
			SerializableDataType.condition(ClassUtil.castClass(ConditionFactory.Instance.class), ConditionTypes.FLUID);

	public static final SerializableDataType<List<ConditionFactory<FluidState>.Instance>> FLUID_CONDITIONS =
			SerializableDataType.list(FLUID_CONDITION);

	public static final SerializableDataType<ConditionFactory<Pair<DamageSource, Float>>.Instance> DAMAGE_CONDITION =
			SerializableDataType.condition(ClassUtil.castClass(ConditionFactory.Instance.class), ConditionTypes.DAMAGE);

	public static final SerializableDataType<List<ConditionFactory<Pair<DamageSource, Float>>.Instance>> DAMAGE_CONDITIONS =
			SerializableDataType.list(DAMAGE_CONDITION);

	public static final SerializableDataType<ConditionFactory<Biome>.Instance> BIOME_CONDITION =
			SerializableDataType.condition(ClassUtil.castClass(ConditionFactory.Instance.class), ConditionTypes.BIOME);

	public static final SerializableDataType<List<ConditionFactory<Biome>.Instance>> BIOME_CONDITIONS =
			SerializableDataType.list(BIOME_CONDITION);

	public static final SerializableDataType<ActionFactory<Entity>.Instance> ENTITY_ACTION =
			SerializableDataType.effect(ClassUtil.castClass(ActionFactory.Instance.class), ActionTypes.ENTITY);

	public static final SerializableDataType<List<ActionFactory<Entity>.Instance>> ENTITY_ACTIONS =
			SerializableDataType.list(ENTITY_ACTION);

	public static final SerializableDataType<ActionFactory<Triple<World, BlockPos, Direction>>.Instance> BLOCK_ACTION =
			SerializableDataType.effect(ClassUtil.castClass(ActionFactory.Instance.class), ActionTypes.BLOCK);

	public static final SerializableDataType<List<ActionFactory<Triple<World, BlockPos, Direction>>.Instance>> BLOCK_ACTIONS =
			SerializableDataType.list(BLOCK_ACTION);

	public static final SerializableDataType<ActionFactory<ItemStack>.Instance> ITEM_ACTION =
			SerializableDataType.effect(ClassUtil.castClass(ActionFactory.Instance.class), ActionTypes.ITEM);

	public static final SerializableDataType<List<ActionFactory<ItemStack>.Instance>> ITEM_ACTIONS =
			SerializableDataType.list(ITEM_ACTION);

	public static final SerializableDataType<Ingredient> INGREDIENT = new SerializableDataType<>(
			Ingredient.class,
			(buffer, ingredient) -> ingredient.write(buffer),
			Ingredient::fromPacket,
			Ingredient::fromJson);

	public static final SerializableDataType<Block> BLOCK = SerializableDataType.registry(Block.class, Registry.BLOCK);

	public static final SerializableDataType<HudRender> HUD_RENDER = SerializableDataType.compound(HudRender.class, new
					SerializableData()
					.add("should_render", BOOLEAN, true)
					.add("bar_index", INT, 0)
					.add("sprite_location", IDENTIFIER, Origins.identifier("textures/gui/resource_bar.png"))
					.add("condition", ENTITY_CONDITION, null),
			(dataInst) -> new HudRender(
					dataInst.getBoolean("should_render"),
					dataInst.getInt("bar_index"),
					dataInst.getId("sprite_location"),
					(ConditionFactory<LivingEntity>.Instance) dataInst.get("condition")),
			(data, inst) -> {
				SerializableData.Instance dataInst = data.new Instance();
				dataInst.set("should_render", inst.shouldRender());
				dataInst.set("bar_index", inst.getBarIndex());
				dataInst.set("sprite_location", inst.getSpriteLocation());
				dataInst.set("condition", inst.getCondition());
				return dataInst;
			});

	public static final SerializableDataType<EntityGroup> ENTITY_GROUP =
			SerializableDataType.mapped(EntityGroup.class, HashBiMap.create(ImmutableMap.of(
					"default", EntityGroup.DEFAULT,
					"undead", EntityGroup.UNDEAD,
					"arthropod", EntityGroup.ARTHROPOD,
					"illager", EntityGroup.ILLAGER,
					"aquatic", EntityGroup.AQUATIC
			)));

	public static final SerializableDataType<EquipmentSlot> EQUIPMENT_SLOT = SerializableDataType.enumValue(EquipmentSlot.class);

	public static final SerializableDataType<SoundEvent> SOUND_EVENT = SerializableDataType.registry(SoundEvent.class, Registry.SOUND_EVENT);

	public static final SerializableDataType<EntityType<?>> ENTITY_TYPE = SerializableDataType.registry(ClassUtil.castClass(EntityType.class), Registry.ENTITY_TYPE);

	public static final SerializableDataType<ParticleType<?>> PARTICLE_TYPE = SerializableDataType.registry(ClassUtil.castClass(ParticleType.class), Registry.PARTICLE_TYPE);

	public static final SerializableDataType<CompoundTag> NBT = SerializableDataType.wrap(CompoundTag.class, SerializableDataType.STRING,
			CompoundTag::toString,
			(str) -> {
				try {
					return new StringNbtReader(new StringReader(str)).parseCompoundTag();
				} catch (CommandSyntaxException e) {
					throw new JsonSyntaxException("Could not parse NBT tag, exception: " + e.getMessage());
				}
			});

	public static final SerializableDataType<ItemStack> ITEM_STACK = SerializableDataType.compound(ItemStack.class,
			new SerializableData()
					.add("item", SerializableDataType.ITEM)
					.add("amount", SerializableDataType.INT, 1)
					.add("tag", NBT, null),
			(data) -> {
				ItemStack stack = new ItemStack(data.get("item"), data.getInt("amount"));
				if (data.isPresent("tag")) {
					stack.setTag(data.get("tag"));
				}
				return stack;
			},
			((serializableData, itemStack) -> {
				SerializableData.Instance data = serializableData.new Instance();
				data.set("item", itemStack.getItem());
				data.set("amount", itemStack.getCount());
				data.set("tag", itemStack.hasTag() ? itemStack.getTag() : null);
				return data;
			}));

	public static final SerializableDataType<List<ItemStack>> ITEM_STACKS = SerializableDataType.list(ITEM_STACK);

	public static final SerializableDataType<Pair<Integer, ItemStack>> POSITIONED_ITEM_STACK = SerializableDataType.compound(ClassUtil.castClass(Pair.class),
			new SerializableData()
					.add("item", SerializableDataType.ITEM)
					.add("amount", SerializableDataType.INT, 1)
					.add("tag", NBT, null)
					.add("slot", SerializableDataType.INT, Integer.MIN_VALUE),
			(data) -> {
				ItemStack stack = new ItemStack(data.get("item"), data.getInt("amount"));
				if (data.isPresent("tag")) {
					stack.setTag(data.get("tag"));
				}
				return new Pair<>(data.getInt("slot"), stack);
			},
			((serializableData, positionedStack) -> {
				SerializableData.Instance data = serializableData.new Instance();
				data.set("item", positionedStack.getRight().getItem());
				data.set("amount", positionedStack.getRight().getCount());
				data.set("tag", positionedStack.getRight().hasTag() ? positionedStack.getRight().getTag() : null);
				data.set("slot", positionedStack.getLeft());
				return data;
			}));

	public static final SerializableDataType<List<Pair<Integer, ItemStack>>> POSITIONED_ITEM_STACKS = SerializableDataType.list(POSITIONED_ITEM_STACK);
	public static final SerializableDataType<Active.Key> KEY = SerializableDataType.compound(Active.Key.class,
			new SerializableData()
					.add("key", SerializableDataType.STRING)
					.add("continuous", SerializableDataType.BOOLEAN, false),
			(data) -> {
				Active.Key key = new Active.Key();
				key.key = data.getString("key");
				key.continuous = data.getBoolean("continuous");
				return key;
			},
			((serializableData, key) -> {
				SerializableData.Instance data = serializableData.new Instance();
				data.set("key", key.key);
				data.set("continuous", key.continuous);
				return data;
			}));
	public static final SerializableDataType<Active.Key> BACKWARDS_COMPATIBLE_KEY = new SerializableDataType<>(Active.Key.class,
			KEY.send, KEY.receive, jsonElement -> {
		if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
			String keyString = jsonElement.getAsString();
			Active.Key key = new Active.Key();
			key.key = keyString.equals("secondary") ? "key.origins.secondary_active" : "key.origins.primary_active";
			key.continuous = false;
			return key;
		}
		return KEY.read.apply(jsonElement);
	});
	public static final SerializableDataType<Tag<EntityType<?>>> ENTITY_TAG = SerializableDataType.wrap(ClassUtil.castClass(Tag.class), IDENTIFIER,
			tag -> ServerTagManagerHolder.getTagManager().getEntityTypes().getTagId(tag),
			TagHooks::getEntityTypeOptional);
	public static final SerializableDataType<Recipe> RECIPE = new SerializableDataType<>(Recipe.class,
			(buffer, recipe) -> {
				buffer.writeIdentifier(Registry.RECIPE_SERIALIZER.getId(recipe.getSerializer()));
				buffer.writeIdentifier(recipe.getId());
				recipe.getSerializer().write(buffer, recipe);
			},
			(buffer) -> {
				Identifier recipeSerializerId = buffer.readIdentifier();
				Identifier recipeId = buffer.readIdentifier();
				RecipeSerializer serializer = Registry.RECIPE_SERIALIZER.get(recipeSerializerId);
				return serializer.read(recipeId, buffer);
			},
			(jsonElement) -> {
				if (!jsonElement.isJsonObject()) {
					throw new RuntimeException("Expected recipe to be a JSON object.");
				}
				JsonObject json = jsonElement.getAsJsonObject();
				Identifier recipeSerializerId = Identifier.tryParse(JsonHelper.getString(json, "type"));
				Identifier recipeId = Identifier.tryParse(JsonHelper.getString(json, "id"));
				RecipeSerializer serializer = Registry.RECIPE_SERIALIZER.get(recipeSerializerId);
				return serializer.read(recipeId, json);
			});
	public static final SerializableDataType<ItemStack> ITEM_OR_ITEM_STACK = new SerializableDataType<>(ItemStack.class,
			ITEM_STACK.send, ITEM_STACK.receive, jsonElement -> {
		if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
			Item item = ITEM.read(jsonElement);
			return new ItemStack(item);
		}
		return ITEM_STACK.read.apply(jsonElement);
	});

	/**
	 * Generates a serializable data type representing a list out of a single data type.<br>
	 * If the source type has a codec, the list will be represented as a codec.
	 *
	 * @param singleDataType
	 * @param <T>
	 *
	 * @return
	 */
	public static <T> SerializableDataType<List<T>> list(SerializableDataType<T> singleDataType) {
		Class<List<T>> cls = ClassUtil.castClass(List.class);
		return singleDataType.getCodec().map(c -> new SerializableDataType<>(cls, OriginsCodecs.listOf(c))).orElseGet(() -> new SerializableDataType<>(cls, (buf, list) -> {
			buf.writeInt(list.size());
			for (T elem : list) {
				singleDataType.send(buf, elem);
			}
		}, (buf) -> {
			int count = buf.readInt();
			LinkedList<T> list = new LinkedList<>();
			for (int i = 0; i < count; i++) {
				list.add(singleDataType.receive(buf));
			}
			return list;
		}, (json) -> {
			LinkedList<T> list = new LinkedList<>();
			if (json.isJsonArray()) {
				for (JsonElement je : json.getAsJsonArray()) {
					T res = singleDataType.read(je);
					if (res != null)
						list.add(res);
				}
			} else {
				T res = singleDataType.read(json);
				if (res != null)
					list.add(res);
			}
			return list;
		}));
	}

	public static <T> SerializableDataType<Optional<T>> optionalRegistry(Class<T> dataClass, Registry<T> registry) {
		return new SerializableDataType<>(ClassUtil.castClass(Optional.class), OriginsCodecs.optionalRegistry(registry::getOrEmpty, registry::getId));
	}

	public static <T> SerializableDataType<T> codecRegistry(Class<T> dataClass, Registry<T> registry) {
		return new SerializableDataType<>(ClassUtil.castClass(Optional.class), registry);
	}

	/**
	 * Kept for backwards compatibility purposes.
	 *
	 * @deprecated Use either {@link #codecRegistry(Class, Registry)} or {@link #optionalRegistry(Class, Registry)}
	 * if you need to acces registries.
	 */
	@Deprecated
	public static <T> SerializableDataType<T> registry(Class<T> dataClass, Registry<T> registry) {
		return new SerializableDataType<>(dataClass,
				(buf, t) -> buf.writeIdentifier(registry.getId(t)),
				(buf) -> registry.get(buf.readIdentifier()),
				(json) -> {
					boolean isOptional = false;
					Identifier id;
					if (json.isJsonPrimitive()) {
						id = Identifier.tryParse(json.getAsString());
					} else if (json.isJsonObject()) {
						JsonObject obj = json.getAsJsonObject();
						id = Identifier.tryParse(obj.get("name").getAsString());
						if (obj.has("platform")) {
							if (!obj.get("platform").getAsString().equalsIgnoreCase(Platform.getModLoader()))
								return null;
						}
						isOptional = obj.has("optional") && obj.get("optional").getAsBoolean();
					} else
						throw new RuntimeException("Json entry was neither a string nor an object.");
					if (!registry.getIds().contains(id)) {
						if (isOptional)
							return null;
						throw new RuntimeException(
								"Identifier \"" + id + "\" was not registered in registry \"" + registry.getKey().getValue() + "\".");
					}
					return registry.get(id);
				});
	}

	public static <T> SerializableDataType<T> compound(Class<T> dataClass, SerializableData data, Function<SerializableData.Instance, T> toInstance, BiFunction<SerializableData, T, SerializableData.Instance> toData) {
		Optional<Codec<T>> codec = data.tryMakeCodec(toInstance, toData);
		return codec.map(tCodec -> new SerializableDataType<>(dataClass, tCodec))
				.orElseGet(() -> new SerializableDataType<>(dataClass,
						(buf, t) -> data.write(buf, toData.apply(data, t)),
						(buf) -> toInstance.apply(data.read(buf)),
						(json) -> toInstance.apply(data.read(json.getAsJsonObject()))));
	}

	public static <T extends Enum<T>> SerializableDataType<T> enumValue(Class<T> dataClass) {
		return enumValue(dataClass, ImmutableMap.of());
	}

	public static <T extends Enum<T>> SerializableDataType<T> enumValue(Class<T> dataClass, Map<String, T> additionalMap) {
		return new SerializableDataType<>(dataClass, OriginsCodecs.enumCodec(dataClass.getEnumConstants(), additionalMap));
	}

	public static <T> SerializableDataType<T> mapped(Class<T> dataClass, BiMap<String, T> map) {
		return new SerializableDataType<>(dataClass, Codec.STRING.xmap(map::get, map.inverse()::get));
	}

	public static <T> SerializableDataType<ConditionFactory<T>.Instance> condition(Class<ConditionFactory<T>.Instance> dataClass, ConditionType<T> conditionType) {
		return new SerializableDataType<>(dataClass, conditionType::write, conditionType::read, conditionType::read);
	}

	public static <T> SerializableDataType<ActionFactory<T>.Instance> effect(Class<ActionFactory<T>.Instance> dataClass, ActionType<T> actionType) {
		return new SerializableDataType<>(dataClass, actionType::write, actionType::read, actionType::read);
	}

	public static <T, U> SerializableDataType<T> wrap(Class<T> dataClass, SerializableDataType<U> base, Function<T, U> toFunction, Function<U, T> fromFunction) {
		return new SerializableDataType<>(dataClass,
				(buf, t) -> base.send(buf, toFunction.apply(t)),
				(buf) -> fromFunction.apply(base.receive(buf)),
				(json) -> fromFunction.apply(base.read(json)));
	}

	public static <T> SerializableDataType<FilterableWeightedList<T>> weightedList(SerializableDataType<T> base) {
		return base.getCodec().map(codec -> RecordCodecBuilder.<Pair<T, Integer>>create(instance -> instance.group(
				codec.fieldOf("element").forGetter(Pair::getLeft),
				Codec.INT.fieldOf("weight").forGetter(Pair::getRight)
		).apply(instance, Pair::new)).listOf().xmap(pairs -> {
			FilterableWeightedList<T> ls = new FilterableWeightedList<>();
			pairs.forEach(x -> ls.add(x.getLeft(), x.getRight()));
			return ls;
		}, x -> x.entryStream().map(p -> new Pair<>(p.getElement(), p.weight)).collect(Collectors.toList())))
				.map(x -> new SerializableDataType<>(ClassUtil.castClass(FilterableWeightedList.class), x))
				.orElseGet(() -> new SerializableDataType<>(ClassUtil.castClass(FilterableWeightedList.class), (buf, list) -> {
			buf.writeInt(list.size());
			list.entryStream().forEach((entry) -> {
				base.send(buf, entry.getElement());
				buf.writeInt(entry.weight);
			});
		}, (buf) -> {
			int count = buf.readInt();
			FilterableWeightedList<T> list = new FilterableWeightedList<>();

			for (int i = 0; i < count; ++i) {
				T t = base.receive(buf);
				int weight = buf.readInt();
				list.add(t, weight);
			}

			return list;
		}, (json) -> {
			FilterableWeightedList<T> list = new FilterableWeightedList<>();
			if (json.isJsonArray()) {
				for (JsonElement je : json.getAsJsonArray()) {
					JsonObject weightedObj = je.getAsJsonObject();
					T elem = base.read(weightedObj.get("element"));
					int weight = JsonHelper.getInt(weightedObj, "weight");
					list.add(elem, weight);
				}
			}

			return list;
		}));
	}

	private static <T> BiConsumer<PacketByteBuf, T> networkWriter(Codec<T> codec) {
		return (buffer, t) -> {
			try {
				buffer.encode(codec, t);
			} catch (IOException e) {
				Origins.LOGGER.error("Failed to write: " + t, e);
				throw new RuntimeException(e);
			}
		};
	}

	private static <T> Function<PacketByteBuf, T> networkReader(Codec<T> codec) {
		return (buffer) -> {
			try {
				return buffer.decode(codec);
			} catch (IOException e) {
				Origins.LOGGER.error("Failed to read: " + codec, e);
				throw new RuntimeException(e);
			}
		};
	}

	private static <T> Function<JsonElement, T> jsonReader(Codec<T> codec) {
		return jsonElement -> codec.parse(InlineJsonOps.INSTANCE, jsonElement).getOrThrow(false, s -> {});
	}

	public static SerializableDataType<RegistryKey<World>> DIMENSION = SerializableDataType.wrap(
			ClassUtil.castClass(RegistryKey.class),
			SerializableDataType.IDENTIFIER,
			RegistryKey::getValue, identifier -> RegistryKey.of(Registry.DIMENSION, identifier)
	);
	private final Class<T> dataClass;
	private final BiConsumer<PacketByteBuf, T> send;
	private final Function<PacketByteBuf, T> receive;
	private final Function<JsonElement, T> read;
	private final Codec<T> codec;

	public SerializableDataType(Class<T> dataClass, Codec<T> codec) {
		this.dataClass = dataClass;
		this.codec = codec;
		this.send = networkWriter(codec);
		this.receive = networkReader(codec);
		this.read = jsonReader(codec);
	}

	public SerializableDataType(Class<T> dataClass,
								BiConsumer<PacketByteBuf, T> send,
								Function<PacketByteBuf, T> receive,
								Function<JsonElement, T> read) {
		this.dataClass = dataClass;
		this.send = send;
		this.receive = receive;
		this.read = read;
		this.codec = null;
	}

	public void send(PacketByteBuf buffer, Object value) {
		send.accept(buffer, cast(value));
	}

	public T receive(PacketByteBuf buffer) {
		return receive.apply(buffer);
	}

	public T read(JsonElement jsonElement) {
		return read.apply(jsonElement);
	}

	public T cast(Object data) {
		return dataClass.cast(data);
	}

	public boolean hasCodec() {
		return this.codec != null;
	}

	public Optional<Codec<T>> getCodec() {
		return Optional.ofNullable(this.codec);
	}

	public JsonElement write(T object) {
		if (!this.hasCodec())
			throw new IllegalArgumentException("Writing for type: " + this.dataClass + " isn't implemented.");
		return this.codec.encodeStart(InlineJsonOps.INSTANCE, object).getOrThrow(false, s -> Origins.LOGGER.error("Failed to serialize: \"{}\": {}", object, s));
	}
}
