package io.github.apace100.origins.power.factory;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.*;
import io.github.apace100.origins.power.factories.WalkOnFluidPower;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Pair;

import java.util.List;

public class PowerFactories {

    @SuppressWarnings("unchecked")
    public static void register() {
        register(new PowerFactory<>(Origins.identifier("launch"),
            new SerializableData()
                .add("cooldown", SerializableDataType.INT)
                .add("speed", SerializableDataType.FLOAT)
                .add("sound", SerializableDataType.SOUND_EVENT, null)
                .add("hud_render", SerializableDataType.HUD_RENDER)
                .add("key", SerializableDataType.BACKWARDS_COMPATIBLE_KEY, new Active.Key()),
            data -> {
                SoundEvent soundEvent = data.get("sound");
                return (type, player) -> {
                    ActiveCooldownPower power = new ActiveCooldownPower(type, player,
                        data.getInt("cooldown"),
                            data.get("hud_render"),
                        e -> {
                            if (!e.world.isClient && e instanceof PlayerEntity) {
                                PlayerEntity p = (PlayerEntity) e;
                                p.addVelocity(0, data.getFloat("speed"), 0);
                                p.velocityModified = true;
                                if (soundEvent != null) {
                                    p.world.playSound(null, p.getX(), p.getY(), p.getZ(), soundEvent, SoundCategory.NEUTRAL, 0.5F, 0.4F / (p.getRandom().nextFloat() * 0.4F + 0.8F));
                                }
                                for (int i = 0; i < 4; ++i) {
                                    ((ServerWorld) p.world).spawnParticles(ParticleTypes.CLOUD, p.getX(), p.getRandomBodyY(), p.getZ(), 8, p.getRandom().nextGaussian(), 0.0D, p.getRandom().nextGaussian(), 0.5);
                                }
                            }
                        });
                    power.setKey(data.get("key"));
                    return power;
                };
            }).allowCondition());
        register(new PowerFactory<>(Origins.identifier("stacking_status_effect"),
            new SerializableData()
                .add("min_stacks", SerializableDataType.INT)
                .add("max_stacks", SerializableDataType.INT)
                .add("duration_per_stack", SerializableDataType.INT)
                .add("effect", SerializableDataType.STATUS_EFFECT_INSTANCE, null)
                .add("effects", SerializableDataType.STATUS_EFFECT_INSTANCES, null),
            data ->
                (type, player) -> {
                    StackingStatusEffectPower power = new StackingStatusEffectPower(type, player,
                        data.getInt("min_stacks"),
                        data.getInt("max_stacks"),
                        data.getInt("duration_per_stack"));
                    if(data.isPresent("effect")) {
                        power.addEffect((StatusEffectInstance)data.get("effect"));
                    }
                    if(data.isPresent("effects")) {
                        ((List<StatusEffectInstance>)data.get("effects")).forEach(power::addEffect);
                    }
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("starting_equipment"),
            new SerializableData()
                .add("stack", SerializableDataType.POSITIONED_ITEM_STACK, null)
                .add("stacks", SerializableDataType.POSITIONED_ITEM_STACKS, null)
                .add("recurrent", SerializableDataType.BOOLEAN, false),
            data ->
                (type, player) -> {
                    StartingEquipmentPower power = new StartingEquipmentPower(type, player);
                    if(data.isPresent("stack")) {
                        Pair<Integer, ItemStack> stack = data.get("stack");
                        int slot = stack.getLeft();
                        if(slot > Integer.MIN_VALUE) {
                            power.addStack(stack.getLeft(), stack.getRight());
                        } else {
                            power.addStack(stack.getRight());
                        }
                    }
                    if(data.isPresent("stacks")) {
                        ((List<Pair<Integer, ItemStack>>)data.get("stacks"))
                            .forEach(integerItemStackPair -> {
                                int slot = integerItemStackPair.getLeft();
                                if(slot > Integer.MIN_VALUE) {
                                    power.addStack(integerItemStackPair.getLeft(), integerItemStackPair.getRight());
                                } else {
                                    power.addStack(integerItemStackPair.getRight());
                                }
                            });
                    }
                    power.setRecurrent(data.getBoolean("recurrent"));
                    return power;
                }));
    }

    private static void register(PowerFactory<?> serializer) {
        ModRegistriesArchitectury.POWER_FACTORY.register(serializer.getSerializerId(), () -> serializer);
    }
}
