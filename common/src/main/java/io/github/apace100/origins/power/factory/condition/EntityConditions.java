package io.github.apace100.origins.power.factory.condition;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.access.MovingEntity;
import io.github.apace100.origins.power.factory.MetaFactories;
import io.github.apace100.origins.power.condition.entity.*;
import io.github.apace100.origins.power.factory.meta.condition.FloatComparingCondition;
import io.github.apace100.origins.power.factory.meta.condition.IntComparingCondition;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.apace100.origins.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;

import java.util.function.Predicate;

public class EntityConditions {

    public static void register() {
        MetaFactories.defineMetaConditions(ModRegistriesArchitectury.ENTITY_CONDITION, OriginsCodecs.ENTITY_CONDITION);
        register("block_collision", BlockCollisionCondition.CODEC);
        register("brightness", FloatComparingCondition.codec(Entity::getBrightnessAtEyes));
        register("daytime", entity -> entity.world.getTimeOfDay() % 24000L < 13000L);
        register("brightness", IntComparingCondition.codec(entity -> Math.toIntExact(entity.world.getTimeOfDay() % 24000L)));
        register("fall_flying", LivingEntity::isFallFlying);
        register("exposed_to_sun",  entity -> {
            if (!entity.world.isDay() || entity.isBeingRainedOn())
                return false;
            BlockPos bp = new BlockPos(entity.getX(), (double) Math.round(entity.getY()), entity.getZ());
            if (entity.getVehicle() instanceof BoatEntity) bp = bp.up();
            return entity.getBrightnessAtEyes() > 0.5F && entity.world.isSkyVisible(bp);
        });
        register("in_rain", Entity::isBeingRainedOn);
        register("invisible", Entity::isInvisible);
        register("on_fire", Entity::isOnFire);
        register("exposed_to_sky", entity -> {
            BlockPos blockPos = entity.getVehicle() instanceof BoatEntity ? (new BlockPos(entity.getX(), (double) Math.round(entity.getY()), entity.getZ())).up() : new BlockPos(entity.getX(), (double) Math.round(entity.getY()), entity.getZ());
            return entity.world.isSkyVisible(blockPos);
        });
        register("sneaking", Entity::isSneaking);
        register("sprinting", Entity::isSprinting);
        register("power_active", PowerActiveCondition.CODEC);
        register("status_effect", StatusEffectCondition.CODEC);
        register("submerged_in", SubmergedInCondition.CODEC);
        register("fluid_height", FluidHeightCondition.CODEC);
        register("origin", OriginCondition.CODEC);
        register("power", PowerCondition.CODEC);
        register("food_level", IntComparingCondition.codec(x -> x instanceof PlayerEntity ? ((PlayerEntity) x).getHungerManager().getFoodLevel() : Integer.MIN_VALUE));
        register("saturation_level", FloatComparingCondition.codec(x -> x instanceof PlayerEntity ? ((PlayerEntity) x).getHungerManager().getSaturationLevel() : Float.NaN));
        register("on_block", OnBlockCondition.CODEC);
        register("equipped_item", EquippedItemCondition.CODEC);
        register("attribute", AttributeCondition.CODEC);
        register("swimming", Entity::isSwimming);
        register("resource", ResourceCondition.CODEC);
        register("air", IntComparingCondition.codec(Entity::getAir));
        register("in_block", InBlockCondition.CODEC);
        register("block_in_radius", BlockInRadiusCondition.CODEC);
        register("dimension", DimensionCondition.CODEC);
        register("xp_levels", IntComparingCondition.codec(t -> t instanceof PlayerEntity ? ((PlayerEntity) t).experienceLevel : Integer.MIN_VALUE));
        register("xp_points", IntComparingCondition.codec(t -> t instanceof PlayerEntity ? ((PlayerEntity) t).totalExperience : Integer.MIN_VALUE));
        register("health",FloatComparingCondition.codec(LivingEntity::getHealth));
        register("relative_health",FloatComparingCondition.codec(t -> t.getHealth() / t.getMaxHealth()));
        register("biome", BiomeCondition.CODEC);
        register("entity_type", EntityTypeCondition.CODEC);
        register("scoreboard", ScoreboardCondition.CODEC);
        register("command", CommandCondition.CODEC);
        register("predicate", PredicateCondition.CODEC);
        register("fall_distance", FloatComparingCondition.codec(t -> t.fallDistance));
        register("collided_horizontally", t -> t.horizontalCollision);
        register("in_block_anywhere", InBlockAnywhereCondition.CODEC);
        register("entity_group", EntityGroupCondition.CODEC);
        register("in_tag", InTagCondition.CODEC);
        register("climbing", LivingEntity::isClimbing);
        register("tamed", x -> (x instanceof TameableEntity) && ((TameableEntity) x).isTamed());
        register("using_item", UsingItemCondition.CODEC);
        register("moving", x -> ((MovingEntity)x).isMoving());
        register("enchantment", EnchantmentCondition.CODEC);
    }

    private static void register(String name, Codec<? extends Predicate<LivingEntity>> codec) {
        ModRegistriesArchitectury.ENTITY_CONDITION.registerSupplied(Origins.identifier(name), () -> new ConditionFactory<>(codec));
    }

    private static void register(String name, Predicate<LivingEntity> codec) {
        register(name, Codec.unit(codec));
    }
}
