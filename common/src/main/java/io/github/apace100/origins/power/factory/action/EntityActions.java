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
        register("block_action_at", BlockActionAtAction.CODEC);
        register("spawn_effect_cloud", SpawnEffectCloudAction.CODEC);
        register("extinguish", Codec.unit(Entity::extinguish));
        register("execute_command", ExecuteCommandEntityAction.CODEC);
        register("change_resource", ChangeResourceAction.CODEC);
        register("feed", FeedAction.CODEC);
        register("add_xp", AddXPAction.CODEC);
        register("set_fall_distance", SetFallDistanceAction.CODEC);
        register("give", GiveAction.CODEC);
        register("equipped_item_action", EquippedItemAction.CODEC);
        register("trigger_cooldown", TriggerCooldownAction.CODEC);
    }

    private static void register(String name, Codec<? extends Consumer<Entity>> codec) {
        ModRegistriesArchitectury.ENTITY_ACTION.registerSupplied(Origins.identifier(name), () -> new ActionFactory<>(codec));
    }
}
