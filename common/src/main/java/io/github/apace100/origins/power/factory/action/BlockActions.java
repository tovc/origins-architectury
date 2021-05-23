package io.github.apace100.origins.power.factory.action;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.block.*;
import io.github.apace100.origins.power.factory.MetaFactories;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.apace100.origins.util.FilterableWeightedList;
import io.github.apace100.origins.util.OriginsCodecs;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import me.shedaniel.architectury.platform.Mod;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class BlockActions {

    public static void register() {
        MetaFactories.defineMetaActions(ModRegistriesArchitectury.BLOCK_ACTION, OriginsCodecs.BLOCK_ACTION, OriginsCodecs.BLOCK_CONDITION, x -> new CachedBlockPosition(x.getLeft(), x.getMiddle(), true));
        register("offset", OffsetAction.CODEC);
        register("set_block", SetBlockAction.CODEC);
        register("add_block", AddBlockAction.CODEC);
        register("execute_command", ExecuteCommandBlockAction.CODEC);
    }

    private static void register(String name, Codec<? extends Consumer<Triple<World, BlockPos, Direction>>> codec) {
        ModRegistriesArchitectury.BLOCK_ACTION.registerSupplied(Origins.identifier(name), () -> new ActionFactory<>(codec));
    }
}
