package io.github.apace100.origins.power.factory.condition;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.access.MovingEntity;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.power.*;
import io.github.apace100.origins.power.condition.MetaFactories;
import io.github.apace100.origins.power.condition.entity.BlockCollisionCondition;
import io.github.apace100.origins.power.condition.entity.PowerActiveCondition;
import io.github.apace100.origins.power.condition.meta.FloatComparingCondition;
import io.github.apace100.origins.power.condition.meta.IntComparingCondition;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.apace100.origins.util.*;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.function.Predicate;

public class EntityConditions {

    @SuppressWarnings("unchecked")
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
        register(new ConditionFactory<>(Origins.identifier("status_effect"), new SerializableData()
            .add("effect", SerializableDataType.STATUS_EFFECT)
            .add("min_amplifier", SerializableDataType.INT, 0)
            .add("max_amplifier", SerializableDataType.INT, Integer.MAX_VALUE)
            .add("min_duration", SerializableDataType.INT, 0)
            .add("max_duration", SerializableDataType.INT, Integer.MAX_VALUE),
            (data, entity) -> {
                StatusEffect effect = data.get("effect");
                if(effect == null) {
                    return false;
                }
                if(entity.hasStatusEffect(effect)) {
                    StatusEffectInstance instance = entity.getStatusEffect(effect);
                    return instance.getDuration() <= data.getInt("max_duration") && instance.getDuration() >= data.getInt("min_duration")
                        && instance.getAmplifier() <= data.getInt("max_amplifier") && instance.getAmplifier() >= data.getInt("min_amplifier");
                }
                return false;
            }));
        register(new ConditionFactory<>(Origins.identifier("submerged_in"), new SerializableData().add("fluid", SerializableDataType.FLUID_TAG),
            (data, entity) -> entity.isSubmergedIn(data.get("fluid"))));
        register(new ConditionFactory<>(Origins.identifier("fluid_height"), new SerializableData()
            .add("fluid", SerializableDataType.FLUID_TAG)
            .add("comparison", SerializableDataType.COMPARISON)
            .add("compare_to", SerializableDataType.DOUBLE),
            (data, entity) -> ((Comparison)data.get("comparison")).compare(entity.getFluidHeight((Tag<Fluid>)data.get("fluid")), data.getDouble("compare_to"))));
        register(new ConditionFactory<>(Origins.identifier("origin"), new SerializableData()
            .add("origin", SerializableDataType.IDENTIFIER)
            .add("layer", SerializableDataType.IDENTIFIER, null),
            (data, entity) -> {
                OriginComponent component = ModComponentsArchitectury.getOriginComponent(entity);
                Identifier originId = data.getId("origin");
                if(data.isPresent("layer")) {
                    Identifier layerId = data.getId("layer");
                    OriginLayer layer = OriginLayers.getLayer(layerId);
                    if(layer == null) {
                        return false;
                    } else {
                        Origin origin = component.getOrigin(layer);
                        if(origin != null) {
                            return origin.getIdentifier().equals(originId);
                        }
                        return false;
                    }
                } else {
                    return component.getOrigins().values().stream().anyMatch(o -> o.getIdentifier().equals(originId));
                }
            }));
        register(new ConditionFactory<>(Origins.identifier("power"), new SerializableData()
            .add("power", SerializableDataType.IDENTIFIER),
            (data, entity) -> {
                try {
                    PowerType<?> powerType = PowerTypeRegistry.get(data.getId("power"));
                    return ModComponentsArchitectury.getOriginComponent(entity).hasPower(powerType);
                } catch(IllegalArgumentException e) {
                    return false;
                }
            }));
        register("food_level", IntComparingCondition.codec(x -> x instanceof PlayerEntity ? ((PlayerEntity) x).getHungerManager().getFoodLevel() : Integer.MIN_VALUE));
        register("saturation_level", FloatComparingCondition.codec(x -> x instanceof PlayerEntity ? ((PlayerEntity) x).getHungerManager().getSaturationLevel() : Float.NaN));
        register(new ConditionFactory<>(Origins.identifier("saturation_level"), new SerializableData()
            .add("comparison", SerializableDataType.COMPARISON)
            .add("compare_to", SerializableDataType.FLOAT),
            (data, entity) -> {
                if(entity instanceof PlayerEntity) {
                    return ((Comparison) data.get("comparison")).compare(((PlayerEntity)entity).getHungerManager().getSaturationLevel(), data.getFloat("compare_to"));
                }
                return false;
            }));
        register(new ConditionFactory<>(Origins.identifier("on_block"), new SerializableData()
            .add("block_condition", SerializableDataType.BLOCK_CONDITION, null),
            (data, entity) -> entity.isOnGround() &&
                (!data.isPresent("block_condition") || ((ConditionFactory<CachedBlockPosition>.Instance)data.get("block_condition")).test(
                    new CachedBlockPosition(entity.world, entity.getBlockPos().down(), true)))));
        register(new ConditionFactory<>(Origins.identifier("equipped_item"), new SerializableData()
            .add("equipment_slot", SerializableDataType.EQUIPMENT_SLOT)
            .add("item_condition", SerializableDataType.ITEM_CONDITION),
            (data, entity) -> ((ConditionFactory<ItemStack>.Instance)data.get("item_condition")).test(
                entity.getEquippedStack((EquipmentSlot)data.get("equipment_slot")))));
        register(new ConditionFactory<>(Origins.identifier("attribute"), new SerializableData()
            .add("attribute", SerializableDataType.ATTRIBUTE)
            .add("comparison", SerializableDataType.COMPARISON)
            .add("compare_to", SerializableDataType.DOUBLE),
            (data, entity) -> {
                double attrValue = 0F;
                EntityAttributeInstance attributeInstance = entity.getAttributeInstance((EntityAttribute) data.get("attribute"));
                if(attributeInstance != null) {
                    attrValue = attributeInstance.getValue();
                }
                return ((Comparison)data.get("comparison")).compare(attrValue, data.getDouble("compare_to"));
            }));
        register("swimming", Entity::isSwimming);
        register(new ConditionFactory<>(Origins.identifier("resource"), new SerializableData()
            .add("resource", SerializableDataType.POWER_TYPE)
            .add("comparison", SerializableDataType.COMPARISON)
            .add("compare_to", SerializableDataType.INT),
            (data, entity) -> {
                int resourceValue = 0;
                OriginComponent component = ModComponentsArchitectury.getOriginComponent(entity);
                Power p = component.getPower((PowerType<?>)data.get("resource"));
                if(p instanceof VariableIntPower) {
                    resourceValue = ((VariableIntPower)p).getValue();
                } else if(p instanceof CooldownPower) {
                    resourceValue = ((CooldownPower)p).getRemainingTicks();
                }
                return ((Comparison)data.get("comparison")).compare(resourceValue, data.getInt("compare_to"));
            }));
        register("air", IntComparingCondition.codec(Entity::getAir));
        register(new ConditionFactory<>(Origins.identifier("in_block"), new SerializableData()
            .add("block_condition", SerializableDataType.BLOCK_CONDITION),
            (data, entity) ->((ConditionFactory<CachedBlockPosition>.Instance)data.get("block_condition")).test(
                new CachedBlockPosition(entity.world, entity.getBlockPos(), true))));
        register(new ConditionFactory<>(Origins.identifier("block_in_radius"), new SerializableData()
            .add("block_condition", SerializableDataType.BLOCK_CONDITION)
            .add("radius", SerializableDataType.INT)
            .add("shape", SerializableDataType.enumValue(Shape.class), Shape.CUBE)
            .add("compare_to", SerializableDataType.INT, 1)
            .add("comparison", SerializableDataType.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL),
            (data, entity) -> {
                Predicate<CachedBlockPosition> blockCondition = data.get("block_condition");
                int stopAt = -1;
                Comparison comparison = data.get("comparison");
                int compareTo = data.getInt("compare_to");
                switch(comparison) {
                    case EQUAL: case LESS_THAN_OR_EQUAL: case GREATER_THAN:
                        stopAt = compareTo + 1;
                        break;
                    case LESS_THAN: case GREATER_THAN_OR_EQUAL:
                        stopAt = compareTo;
                        break;
                }
                int count = 0;
                for(BlockPos pos : Shape.getPositions(entity.getBlockPos(), data.get("shape"), data.getInt("radius"))) {
                    if(blockCondition.test(new CachedBlockPosition(entity.world, pos, true))) {
                        count++;
                        if(count == stopAt) {
                            break;
                        }
                    }
                }
                return comparison.compare(count, compareTo);
            }));
        register(new ConditionFactory<>(Origins.identifier("dimension"), new SerializableData()
            .add("dimension", SerializableDataType.IDENTIFIER),
            (data, entity) -> entity.world.getRegistryKey() == RegistryKey.of(Registry.DIMENSION, data.getId("dimension"))));
        register(new ConditionFactory<>(Origins.identifier("xp_levels"), new SerializableData()
            .add("comparison", SerializableDataType.COMPARISON)
            .add("compare_to", SerializableDataType.INT),
            (data, entity) -> {
                if(entity instanceof PlayerEntity) {
                    return ((Comparison)data.get("comparison")).compare(((PlayerEntity)entity).experienceLevel, data.getInt("compare_to"));
                }
                return false;
            }));
        register("xp_points", IntComparingCondition.codec(t -> t instanceof PlayerEntity ? ((PlayerEntity) t).totalExperience : Integer.MIN_VALUE));
        register("health",FloatComparingCondition.codec(LivingEntity::getHealth));
        register("relative_health",FloatComparingCondition.codec(t -> t.getHealth() / t.getMaxHealth()));
        register(new ConditionFactory<>(Origins.identifier("biome"), new SerializableData()
            .add("biome", SerializableDataType.IDENTIFIER, null)
            .add("biomes", SerializableDataType.IDENTIFIERS, null)
            .add("condition", SerializableDataType.BIOME_CONDITION, null),
            (data, entity) -> {
                Biome biome = entity.world.getBiome(entity.getBlockPos());
                ConditionFactory<Biome>.Instance condition = (ConditionFactory<Biome>.Instance)data.get("condition");
                if(data.isPresent("biome") || data.isPresent("biomes")) {
                    Identifier biomeId = entity.world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
                    if(data.isPresent("biome") && biomeId.equals(data.getId("biome"))) {
                        return condition == null || condition.test(biome);
                    }
                    if(data.isPresent("biomes") && ((List<Identifier>)data.get("biomes")).contains(biomeId)) {
                        return condition == null || condition.test(biome);
                    }
                    return false;
                }
                return condition == null || condition.test(biome);
            }));
        register(new ConditionFactory<>(Origins.identifier("entity_type"), new SerializableData()
            .add("entity_type", SerializableDataType.ENTITY_TYPE),
            (data, entity) -> entity.getType() == data.get("entity_type")));
        register(new ConditionFactory<>(Origins.identifier("scoreboard"), new SerializableData()
            .add("objective", SerializableDataType.STRING)
            .add("comparison", SerializableDataType.COMPARISON)
            .add("compare_to", SerializableDataType.INT),
            (data, entity) -> {
                if(entity instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity)entity;
                    Scoreboard scoreboard = player.getScoreboard();
                    ScoreboardObjective objective = scoreboard.getObjective(data.getString("objective"));
                    String playerName = player.getName().asString();

                    if (scoreboard.playerHasObjective(playerName, objective)) {
                        int value = scoreboard.getPlayerScore(playerName, objective).getScore();
                        return ((Comparison)data.get("comparison")).compare(value, data.getInt("compare_to"));
                    }
                }
                return false;
            }));
        register(new ConditionFactory<>(Origins.identifier("command"), new SerializableData()
            .add("command", SerializableDataType.STRING)
            .add("permission_level", SerializableDataType.INT, 4)
            .add("comparison", SerializableDataType.COMPARISON)
            .add("compare_to", SerializableDataType.INT),
            (data, entity) -> {
                MinecraftServer server = entity.world.getServer();
                if(server != null) {
                    ServerCommandSource source = new ServerCommandSource(
                        CommandOutput.DUMMY,
                        entity.getPos(),
                        entity.getRotationClient(),
                        entity.world instanceof ServerWorld ? (ServerWorld)entity.world : null,
                        data.getInt("permission_level"),
                        entity.getName().getString(),
                        entity.getDisplayName(),
                        server,
                        entity);
                    int output = server.getCommandManager().execute(source, data.getString("command"));
                    return ((Comparison)data.get("comparison")).compare(output, data.getInt("compare_to"));
                }
                return false;
            }));
        register(new ConditionFactory<>(Origins.identifier("predicate"), new SerializableData()
            .add("predicate", SerializableDataType.IDENTIFIER),
            (data, entity) -> {
                MinecraftServer server = entity.world.getServer();
                if (server != null) {
                    LootCondition lootCondition = server.getPredicateManager().get(data.get("predicate"));
                    if (lootCondition != null) {
                        LootContext.Builder lootBuilder = (new LootContext.Builder((ServerWorld) entity.world))
                            .parameter(LootContextParameters.ORIGIN, entity.getPos())
                            .optionalParameter(LootContextParameters.THIS_ENTITY, entity);
                        return lootCondition.test(lootBuilder.build(LootContextTypes.COMMAND));
                    }
                }
                return false;
            }
        ));
        register("fall_distance", FloatComparingCondition.codec(t -> t.fallDistance));
        register("collided_horizontally", t -> t.horizontalCollision);
        register(new ConditionFactory<>(Origins.identifier("in_block_anywhere"), new SerializableData()
            .add("block_condition", SerializableDataType.BLOCK_CONDITION)
            .add("comparison", SerializableDataType.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
            .add("compare_to", SerializableDataType.INT, 1),
            (data, entity) -> {
                Predicate<CachedBlockPosition> blockCondition = data.get("block_condition");
                int stopAt = -1;
                Comparison comparison = data.get("comparison");
                int compareTo = data.getInt("compare_to");
                switch(comparison) {
                    case EQUAL: case LESS_THAN_OR_EQUAL: case GREATER_THAN:
                        stopAt = compareTo + 1;
                        break;
                    case LESS_THAN: case GREATER_THAN_OR_EQUAL:
                        stopAt = compareTo;
                        break;
                }
                int count = 0;
                Box box = entity.getBoundingBox();
                BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D);
                BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, Math.min(box.maxY - 0.001D, entity.world.getHeight()), box.maxZ - 0.001D);
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                for(int i = blockPos.getX(); i <= blockPos2.getX() && count < stopAt; ++i) {
                    for(int j = blockPos.getY(); j <= blockPos2.getY() && count < stopAt; ++j) {
                        for(int k = blockPos.getZ(); k <= blockPos2.getZ() && count < stopAt; ++k) {
                            mutable.set(i, j, k);
                            if(blockCondition.test(new CachedBlockPosition(entity.world, mutable, false))) {
                                count++;
                            }
                        }
                    }
                }
                return comparison.compare(count, compareTo);}));
        register(new ConditionFactory<>(Origins.identifier("entity_group"), new SerializableData()
            .add("group", SerializableDataType.ENTITY_GROUP),
            (data, entity) -> entity.getGroup() == data.get("group")));
        register(new ConditionFactory<>(Origins.identifier("in_tag"), new SerializableData()
            .add("tag", SerializableDataType.ENTITY_TAG),
            (data, entity) -> ((Tag<EntityType<?>>)data.get("tag")).contains(entity.getType())));
        register(new ConditionFactory<>(Origins.identifier("climbing"), new SerializableData(), (data, entity) -> entity.isClimbing()));
        register(new ConditionFactory<>(Origins.identifier("tamed"), new SerializableData(), (data, entity) -> {
            if(entity instanceof TameableEntity) {
                return ((TameableEntity)entity).isTamed();
            }
            return false;
        }));
        register(new ConditionFactory<>(Origins.identifier("using_item"), new SerializableData()
            .add("item_condition", SerializableDataType.ITEM_CONDITION, null), (data, entity) -> {
            if(entity.isUsingItem()) {
                ConditionFactory<ItemStack>.Instance condition = data.get("item_condition");
                if(condition != null) {
                    Hand activeHand = entity.getActiveHand();
                    ItemStack handStack = entity.getStackInHand(activeHand);
                    return condition.test(handStack);
                } else {
                    return true;
                }
            }
            return false;
        }));
        register(new ConditionFactory<>(Origins.identifier("moving"), new SerializableData(),
            (data, entity) -> ((MovingEntity)entity).isMoving()));
        register(new ConditionFactory<>(Origins.identifier("enchantment"), new SerializableData()
            .add("enchantment", SerializableDataType.ENCHANTMENT)
            .add("comparison", SerializableDataType.COMPARISON)
            .add("compare_to", SerializableDataType.INT)
            .add("calculation", SerializableDataType.STRING, "sum"),
            (data, entity) -> {
                int value = 0;
                Enchantment enchantment = (Enchantment)data.get("enchantment");
                String calculation = data.getString("calculation");
                switch(calculation) {
                    case "sum":
                        for(ItemStack stack : enchantment.getEquipment(entity).values()) {
                            value += EnchantmentHelper.getLevel(enchantment, stack);
                        }
                        break;
                    case "max":
                        value = EnchantmentHelper.getEquipmentLevel(enchantment, entity);
                        break;
                    default:
                        Origins.LOGGER.error("Error in \"enchantment\" entity condition, undefined calculation type: \"" + calculation + "\".");
                        break;
                }
                return ((Comparison)data.get("comparison")).compare(value, data.getInt("compare_to"));
            }));
    }

    private static void register(ConditionFactory<LivingEntity> conditionFactory) {
        ModRegistriesArchitectury.ENTITY_CONDITION.registerSupplied(conditionFactory.getSerializerId(), () -> conditionFactory);
    }

    private static void register(String name, Codec<? extends Predicate<LivingEntity>> codec) {
        ModRegistriesArchitectury.ENTITY_CONDITION.registerSupplied(Origins.identifier(name), () -> new ConditionFactory<>(codec));
    }

    private static void register(String name, Predicate<LivingEntity> codec) {
        register(name, Codec.unit(codec));
    }
}
