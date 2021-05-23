package io.github.apace100.origins.power.factory.action;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.power.CooldownPower;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.VariableIntPower;
import io.github.apace100.origins.power.action.entity.*;
import io.github.apace100.origins.power.factory.MetaFactories;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.apace100.origins.util.*;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class EntityActions {

    public static void register() {
        MetaFactories.defineMetaActions(ModRegistriesArchitectury.ENTITY_ACTION, OriginsCodecs.ENTITY_ACTION, OriginsCodecs.ENTITY_CONDITION, x -> (x instanceof LivingEntity) ? (LivingEntity) x : null);
        register("damage", DamageAction.CODEC);
        register("heal", HealAction.CODEC);
        register("play_sound", PlaySoundAction.CODEC);
        register("exhaust", ExhaustAction.CODEC);
        register("apply_effect", ApplyEffectAction.CODEC);
        register("clear_effect", ClearEffectAction.CODEC);
        register("set_on_fire", SetOnFireAction.CODEC);
        register("add_velocity", AddVelocityAction.CODEC);
        register("spawn_entity", SpawnEntityAction.CODEC);
        register("gain_air", GainAirAction.CODEC);
        register(new ActionFactory<>(Origins.identifier("block_action_at"), new SerializableData()
            .add("block_action", SerializableDataType.BLOCK_ACTION),
            (data, entity) -> data.<ActionFactory<Triple<World, BlockPos, Direction>>.Instance>get("block_action").accept(
                Triple.of(entity.world, entity.getBlockPos(), Direction.UP))));
        register(new ActionFactory<>(Origins.identifier("spawn_effect_cloud"), new SerializableData()
            .add("radius", SerializableDataType.FLOAT, 3.0F)
            .add("radius_on_use", SerializableDataType.FLOAT, -0.5F)
            .add("wait_time", SerializableDataType.INT, 10)
            .add("effect", SerializableDataType.STATUS_EFFECT_INSTANCE, null)
            .add("effects", SerializableDataType.STATUS_EFFECT_INSTANCES, null),
            (data, entity) -> {
                AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(entity.world, entity.getX(), entity.getY(), entity.getZ());
                if (entity instanceof LivingEntity) {
                    areaEffectCloudEntity.setOwner((LivingEntity)entity);
                }
                areaEffectCloudEntity.setRadius(data.getFloat("radius"));
                areaEffectCloudEntity.setRadiusOnUse(data.getFloat("radius_on_use"));
                areaEffectCloudEntity.setWaitTime(data.getInt("wait_time"));
                areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
                List<StatusEffectInstance> effects = new LinkedList<>();
                if(data.isPresent("effect")) {
                    effects.add(data.get("effect"));
                }
                if(data.isPresent("effects")) {
                    effects.addAll(data.get("effects"));
                }
                areaEffectCloudEntity.setColor(PotionUtil.getColor(effects));
                effects.forEach(areaEffectCloudEntity::addEffect);

                entity.world.spawnEntity(areaEffectCloudEntity);
            }));
        register("extinguish", Codec.unit(Entity::extinguish));
        register("execute_command", ExecuteCommandEntityAction.CODEC);
        register(new ActionFactory<>(Origins.identifier("change_resource"), new SerializableData()
            .add("resource", SerializableDataType.POWER_TYPE)
            .add("change", SerializableDataType.INT),
            (data, entity) -> {
                if(entity instanceof PlayerEntity) {
                    OriginComponent component = ModComponentsArchitectury.getOriginComponent(entity);
                    Power p = component.getPower((PowerType<?>)data.get("resource"));
                    if(p instanceof VariableIntPower) {
                        VariableIntPower vip = (VariableIntPower)p;
                        int newValue = vip.getValue() + data.getInt("change");
                        vip.setValue(newValue);
                        OriginComponent.sync((PlayerEntity)entity);
                    } else if(p instanceof CooldownPower) {
                        CooldownPower cp = (CooldownPower)p;
                        cp.modify(data.getInt("change"));
                        OriginComponent.sync((PlayerEntity)entity);
                    }
                }
            }));
        register(new ActionFactory<>(Origins.identifier("feed"), new SerializableData()
            .add("food", SerializableDataType.INT)
            .add("saturation", SerializableDataType.FLOAT),
            (data, entity) -> {
                if(entity instanceof PlayerEntity) {
                    ((PlayerEntity)entity).getHungerManager().add(data.getInt("food"), data.getFloat("saturation"));
                }
            }));
        register(new ActionFactory<>(Origins.identifier("add_xp"), new SerializableData()
            .add("points", SerializableDataType.INT, 0)
            .add("levels", SerializableDataType.INT, 0),
            (data, entity) -> {
                if(entity instanceof PlayerEntity) {
                    int points = data.getInt("points");
                    int levels = data.getInt("levels");
                    if(points > 0) {
                        ((PlayerEntity)entity).addExperience(points);
                    }
                    ((PlayerEntity)entity).addExperienceLevels(levels);
                }
            }));
        register(new ActionFactory<>(Origins.identifier("set_fall_distance"), new SerializableData()
            .add("fall_distance", SerializableDataType.FLOAT),
            (data, entity) -> {
                entity.fallDistance = data.getFloat("fall_distance");
            }));
        register(new ActionFactory<>(Origins.identifier("give"), new SerializableData()
            .add("stack", SerializableDataType.ITEM_STACK),
            (data, entity) -> {
                if(!entity.world.isClient()) {
                    ItemStack stack = (ItemStack)data.get("stack");
                    stack = stack.copy();
                    if(entity instanceof PlayerEntity) {
                        ((PlayerEntity)entity).inventory.offerOrDrop(entity.world, stack);
                    } else {
                        entity.world.spawnEntity(new ItemEntity(entity.world, entity.getX(), entity.getY(), entity.getZ(), stack));
                    }
                }
            }));
        register(new ActionFactory<>(Origins.identifier("equipped_item_action"), new SerializableData()
            .add("equipment_slot", SerializableDataType.EQUIPMENT_SLOT)
            .add("action", SerializableDataType.ITEM_ACTION),
            (data, entity) -> {
                if(entity instanceof LivingEntity) {
                    ItemStack stack = ((LivingEntity)entity).getEquippedStack((EquipmentSlot)data.get("equipment_slot"));
                    ActionFactory<ItemStack>.Instance action = (ActionFactory<ItemStack>.Instance)data.get("action");
                    action.accept(stack);
                }
            }));
        register(new ActionFactory<>(Origins.identifier("trigger_cooldown"), new SerializableData()
            .add("power", SerializableDataType.POWER_TYPE),
            (data, entity) -> {
                if(entity instanceof PlayerEntity) {
                    OriginComponent component = ModComponentsArchitectury.getOriginComponent(entity);
                    Power p = component.getPower((PowerType<?>)data.get("power"));
                    if(p instanceof CooldownPower) {
                        CooldownPower cp = (CooldownPower)p;
                        cp.use();
                    }
                }
            }));
    }

    private static void register(ActionFactory<Entity> actionFactory) {
        ModRegistriesArchitectury.ENTITY_ACTION.register(actionFactory.getSerializerId(), () -> actionFactory);
    }

    private static void register(String name, Codec<? extends Consumer<Entity>> codec) {
        ModRegistriesArchitectury.ENTITY_ACTION.registerSupplied(Origins.identifier(name), () -> new ActionFactory<>(codec));
    }
}
