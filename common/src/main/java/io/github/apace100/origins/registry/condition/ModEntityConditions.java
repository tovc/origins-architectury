package io.github.apace100.origins.registry.condition;

import com.mojang.serialization.MapCodec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.access.MovingEntity;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemCondition;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import io.github.apace100.origins.condition.entity.*;
import io.github.apace100.origins.factory.MetaFactories;
import io.github.apace100.origins.util.OriginsCodecs;
import me.shedaniel.architectury.registry.RegistrySupplier;
import me.shedaniel.architectury.utils.EnvExecutor;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ModEntityConditions {
	public static final BiPredicate<ConfiguredEntityCondition<?, ?>, LivingEntity> PREDICATE = (config, entity) -> config.check(entity);

	public static final RegistrySupplier<SimpleEntityCondition> DAYTIME = register("daytime", entity -> entity.world.getTimeOfDay() % 24000L < 13000L);
	public static final RegistrySupplier<SimpleEntityCondition> FALL_FLYING = register("fall_flying", LivingEntity::isFallFlying);
	public static final RegistrySupplier<SimpleEntityCondition> EXPOSED_TO_SUN = register("exposed_to_sun", entity -> entity.getBrightnessAtEyes() > 0.5F && SimpleEntityCondition.isExposedToSky(entity));
	public static final RegistrySupplier<SimpleEntityCondition> IN_RAIN = register("in_rain", Entity::isBeingRainedOn);
	public static final RegistrySupplier<SimpleEntityCondition> INVISIBLE = register("invisible", Entity::isInvisible);
	public static final RegistrySupplier<SimpleEntityCondition> ON_FIRE = register("on_fire", Entity::isOnFire);
	public static final RegistrySupplier<SimpleEntityCondition> EXPOSED_TO_SKY = register("exposed_to_sky", SimpleEntityCondition::isExposedToSky);
	public static final RegistrySupplier<SimpleEntityCondition> SNEAKING = register("sneaking", Entity::isSneaking);
	public static final RegistrySupplier<SimpleEntityCondition> SPRINTING = register("sprinting", Entity::isSprinting);
	public static final RegistrySupplier<SimpleEntityCondition> SWIMMING = register("swimming", Entity::isSwimming);
	public static final RegistrySupplier<SimpleEntityCondition> COLLIDED_HORIZONTALLY = register("collided_horizontally", t -> t.horizontalCollision);
	public static final RegistrySupplier<SimpleEntityCondition> CLIMBING = register("climbing", LivingEntity::isClimbing);
	public static final RegistrySupplier<SimpleEntityCondition> TAMED = register("tamed", x -> x instanceof TameableEntity te && te.isTamed());
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
	public static final RegistrySupplier<FluidHeightCondition> FLUID_HEIGHT = register("fluid_height", FluidHeightCondition::new);
	public static final RegistrySupplier<OriginCondition> ORIGIN = register("origin", OriginCondition::new);
	public static final RegistrySupplier<SingleFieldEntityCondition<Optional<ConfiguredBlockCondition<?, ?>>>> ON_BLOCK = register("on_block", ConfiguredBlockCondition.CODEC.optionalFieldOf("block_condition"), (entity, configuration) -> entity.isOnGround() && configuration.map(x -> x.check(new CachedBlockPosition(entity.world, entity.getBlockPos(), true))).orElse(true));
	public static final RegistrySupplier<SingleFieldEntityCondition<ConfiguredBlockCondition<?, ?>>> IN_BLOCK = register("in_block", ConfiguredBlockCondition.CODEC.fieldOf("block_condition"), (entity, configuration) -> configuration.check(new CachedBlockPosition(entity.world, entity.getBlockPos(), true)));
	public static final RegistrySupplier<ResourceCondition> RESOURCE = register("resource", ResourceCondition::new);
	public static final RegistrySupplier<SingleFieldEntityCondition<RegistryKey<World>>> DIMENSION = register("dimension", OriginsCodecs.DIMENSION.fieldOf("dimension"), (entity, dimension) -> entity.getEntityWorld().getRegistryKey().equals(dimension));
	public static final RegistrySupplier<SingleFieldEntityCondition<EntityGroup>> ENTITY_GROUP = register("entity_group", OriginsCodecs.ENTITY_GROUP.fieldOf("group"), (entity, group) -> entity.getGroup().equals(group));
	public static final RegistrySupplier<SingleFieldEntityCondition<Optional<ConfiguredItemCondition<?, ?>>>> USING_ITEM = register("using_item", ConfiguredItemCondition.CODEC.optionalFieldOf("item_condition"), (entity, configuration) -> entity.isUsingItem() && configuration.map(x -> x.check(entity.getStackInHand(entity.getActiveHand()))).orElse(true));
	public static final RegistrySupplier<SingleFieldEntityCondition<Identifier>> PREDICATE_CONDITION = register("predicate", Identifier.CODEC.fieldOf("predicate"), SingleFieldEntityCondition::checkPredicate);
	public static final RegistrySupplier<EquippedItemCondition> EQUIPPED_ITEM = register("equipped_item", EquippedItemCondition::new);
	public static final RegistrySupplier<CommandCondition> COMMAND = register("command", CommandCondition::new);
	public static final RegistrySupplier<AttributeCondition> ATTRIBUTE = register("attribute", AttributeCondition::new);
	public static final RegistrySupplier<BlockInRadiusCondition> BLOCK_IN_RADIUS = register("block_in_radius", BlockInRadiusCondition::new);
	public static final RegistrySupplier<BiomeCondition> BIOME = register("biome", BiomeCondition::new);
	public static final RegistrySupplier<ScoreboardCondition> SCOREBOARD = register("scoreboard", ScoreboardCondition::new);
	public static final RegistrySupplier<InBlockAnywhereCondition> IN_BLOCK_ANYWHERE = register("in_block_anywhere", InBlockAnywhereCondition::new);
	public static final RegistrySupplier<UsingEffectiveToolCondition> USING_EFFECTIVE_TOOL = registerSided("using_effective_tool", () -> UsingEffectiveToolCondition::new, () -> UsingEffectiveToolCondition.Client::new);
	public static final RegistrySupplier<AdvancementCondition> ADVANCEMENT = registerSided("advancement", () -> AdvancementCondition::new, () -> AdvancementCondition.Client::new);
	public static final RegistrySupplier<GameModeCondition> GAMEMODE = registerSided("gamemode", () -> GameModeCondition::new, () -> GameModeCondition.Client::new);

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

	private static <T extends EntityCondition<?>> RegistrySupplier<T> registerSided(String name, Supplier<Supplier<T>> client, Supplier<Supplier<T>> server) {
		return register(name, () -> EnvExecutor.getEnvSpecific(client, server));
	}
}
