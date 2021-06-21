package io.github.apace100.origins.registry.condition;

import com.mojang.serialization.MapCodec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.access.MovingEntity;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import io.github.apace100.origins.condition.configuration.StatusEffectConfiguration;
import io.github.apace100.origins.condition.entity.*;
import io.github.apace100.origins.condition.meta.FloatComparingCondition;
import io.github.apace100.origins.condition.meta.IntComparingCondition;
import io.github.apace100.origins.factory.MetaFactories;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.apace100.origins.util.OriginsCodecs;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EntityConditions {
	public static final BiPredicate<ConfiguredEntityCondition<?, ?>, LivingEntity> PREDICATE = (config, entity) -> config.check(entity);)

	public static final RegistrySupplier<SimpleEntityCondition> DAYTIME = register("daytime", entity -> entity.world.getTimeOfDay() % 24000L < 13000L);
	public static final RegistrySupplier<SimpleEntityCondition> FALL_FLYING = register("fall_flying", LivingEntity::isFallFlying);
	public static final RegistrySupplier<SimpleEntityCondition> EXPOSED_TO_SUN = register("exposed_to_sun", entity -> entity.getBrightnessAtEyes() > 0.5F && SimpleEntityCondition.isExposedToSky());
	public static final RegistrySupplier<SimpleEntityCondition> IN_RAIN = register("in_rain", Entity::isBeingRainedOn);
	public static final RegistrySupplier<SimpleEntityCondition> INVISIBLE = register("invisible", Entity::isInvisible);
	public static final RegistrySupplier<SimpleEntityCondition> ON_FIRE = register("on_fire", Entity::isOnFire);
	public static final RegistrySupplier<SimpleEntityCondition> EXPOSED_TO_SKY = register("exposed_to_sky", SimpleEntityCondition::isExposedToSky);
	public static final RegistrySupplier<SimpleEntityCondition> SNEAKING = register("sneaking", Entity::isSneaking);
	public static final RegistrySupplier<SimpleEntityCondition> SPRINTING = register("sprinting", Entity::isSprinting);
	public static final RegistrySupplier<SimpleEntityCondition> SWIMMING = register("swimming", Entity::isSwimming);
	public static final RegistrySupplier<SimpleEntityCondition> COLLIDED_HORIZONTALLY = register("collided_horizontally", t -> t.horizontalCollision);
	public static final RegistrySupplier<SimpleEntityCondition> CLIMBING = register("climbing", LivingEntity::isClimbing);
	public static final RegistrySupplier<SimpleEntityCondition> TAMED = register("tamed", x -> x instanceof TameableEntity te && te.isTamed()));
	public static final RegistrySupplier<SimpleEntityCondition> MOVING = register("moving", x -> ((MovingEntity) x).isMoving());
	public static final RegistrySupplier<FloatComparingEntityCondition> BRIGHTNESS = registerFloat("brightness", Entity::getBrightnessAtEyes);
	public static final RegistrySupplier<FloatComparingEntityCondition> SATURATION_LEVEL = registerFloat("saturation_level", x -> x instanceof PlayerEntity ? ((PlayerEntity) x).getHungerManager().getSaturationLevel() : null);
	public static final RegistrySupplier<FloatComparingEntityCondition> HEALTH = registerFloat("health", LivingEntity::getHealth);
	public static final RegistrySupplier<FloatComparingEntityCondition> RELATIVE_HEALTH = registerFloat("relative_health", t -> t.getHealth() / t.getMaxHealth());
	public static final RegistrySupplier<FloatComparingEntityCondition> FALL_DISTANCE = registerFloat("fall_distance", t -> t.fallDistance);
	public static final RegistrySupplier<IntComparingEntityCondition> TIME_OF_DAY = registerInt("time_of_day", t -> Math.toIntExact(t.world.getTimeOfDay() % 24000L));
	public static final RegistrySupplier<IntComparingEntityCondition> AIR = registerInt("air", Entity::getAir);
	public static final RegistrySupplier<IntComparingEntityCondition> FOOD_LEVEL = registerIntPlayer("food_level", x -> x.getHungerManager().getFoodLevel());
	public static final RegistrySupplier<IntComparingEntityCondition> XP_LEVELS = registerIntPlayer("xp_levels", x -> x.experienceLevel);
	public static final RegistrySupplier<IntComparingEntityCondition> XP_POINTS = registerIntPlayer("xp_points", x -> x.totalExperience);
	public static final RegistrySupplier<EnchantmentCondition> ENCHANTMENT = register("enchantment", EnchantmentCondition::new);
	public static final RegistrySupplier<BlockCollisionCondition> BLOCK_COLLISION = register("block_collision", BlockCollisionCondition::new);
	public static final RegistrySupplier<PowerActiveCondition> POWER_ACTIVE = register("power_active", PowerActiveCondition::new);
	public static final RegistrySupplier<StatusEffectCondition> STATUS_EFFECT = register("status_effect", StatusEffectCondition::new);
	public static final RegistrySupplier<SingleFieldEntityCondition<Tag<Fluid>>> SUBMERGED_IN = register("submerged_in", OriginsCodecs.FLUID_TAG.fieldOf("fluid"), Entity::isSubmergedIn);
	public static final RegistrySupplier<SingleFieldEntityCondition<Optional<EntityType<?>>>> ENTITY_TYPE = register("entity_type", OriginsCodecs.OPTIONAL_ENTITY_TYPE.fieldOf("entity_type"), (entity, o) -> o.map(x -> Objects.equals(x, entity.getType())).orElse(false));
	public static final RegistrySupplier<SingleFieldEntityCondition<Tag<EntityType<?>>>> IN_TAG = register("in_tag", OriginsCodecs.ENTITY_TAG.fieldOf("tag"), (entity, o) -> entity.getType().isIn(o));
	public static final RegistrySupplier<PowerCondition> POWER = register("power", PowerCondition::new);

	public static void register() {
		MetaFactories.defineMetaConditions(ModRegistriesArchitectury.ENTITY_CONDITION, OriginsCodecs.ENTITY_CONDITION);
		register("fluid_height", FluidHeightCondition.CODEC);
		register("origin", OriginCondition.CODEC);
		register("on_block", OnBlockCondition.CODEC);
		register("equipped_item", EquippedItemCondition.CODEC);
		register("attribute", AttributeCondition.CODEC);
		register("resource", ResourceCondition.CODEC);
		register("in_block", InBlockCondition.CODEC);
		register("block_in_radius", BlockInRadiusCondition.CODEC);
		register("dimension", DimensionCondition.CODEC);
		register("biome", BiomeCondition.CODEC);
		register("scoreboard", ScoreboardCondition.CODEC);
		register("command", CommandCondition.CODEC);
		register("predicate", PredicateCondition.CODEC);
		register("in_block_anywhere", InBlockAnywhereCondition.CODEC);
		register("entity_group", EntityGroupCondition.CODEC);
		register("using_item", UsingItemCondition.CODEC);
	}

	public static void register() {
		MetaFactories.defineMetaConditions(OriginsRegistries.ENTITY_CONDITION, DelegatedEntityCondition::new, ConfiguredEntityCondition.CODEC, PREDICATE);
	}

	@SuppressWarnings("unchecked")
	private static <T extends EntityCondition<?>> RegistrySupplier<T> register(String name, Supplier<T> factory) {
		return (RegistrySupplier<T>) OriginsRegistries.ENTITY_CONDITION.registerSupplied(Origins.identifier(name), factory::get);
	}

	private static RegistrySupplier<SimpleEntityCondition> register(String name, Predicate<LivingEntity> factory) {
		return register(name, () -> new SimpleEntityCondition(factory));
	}

	private static RegistrySupplier<IntComparingEntityCondition> registerInt(String name, Function<LivingEntity, Integer> factory) {
		return register(name, () -> new IntComparingEntityCondition(factory));
	}

	private static RegistrySupplier<IntComparingEntityCondition> registerIntPlayer(String name, Function<PlayerEntity, Integer> factory) {
		return registerInt(name, living -> living instanceof PlayerEntity pe ? factory.apply(pe) : null);
	}

	private static RegistrySupplier<FloatComparingEntityCondition> registerFloat(String name, Function<LivingEntity, Float> factory) {
		return register(name, () -> new FloatComparingEntityCondition(factory));
	}

	private static <T> RegistrySupplier<SingleFieldEntityCondition<T>> register(String name, MapCodec<T> codec, BiPredicate<LivingEntity, T> predicate) {
		return register(name, () -> new SingleFieldEntityCondition<>(codec, predicate));
	}

}
