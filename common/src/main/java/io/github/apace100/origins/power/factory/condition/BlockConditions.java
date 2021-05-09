package io.github.apace100.origins.power.factory.condition;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.MetaFactories;
import io.github.apace100.origins.power.condition.block.*;
import io.github.apace100.origins.power.condition.meta.IntComparingCondition;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.pattern.CachedBlockPosition;

import java.util.function.Predicate;

public class BlockConditions {

	public static void register() {
		MetaFactories.defineMetaConditions(ModRegistriesArchitectury.BLOCK_CONDITION, OriginsCodecs.BLOCK_CONDITION);
		register("offset", OffsetCondition.CODEC);
		register("height", IntComparingCondition.codec(x -> x.getBlockPos().getY()));
		register("block", BlockCondition.CODEC);
		register("in_tag", InTagCondition.CODEC);
		register("adjacent", AdjacentCondition.CODEC);
		register("replacable", SimpleBlockConditions.REPLACEABLE);
		register("replaceable", SimpleBlockConditions.REPLACEABLE); //Fixed typo
		register("attachable", AttachableCondition.CODEC);
		register("fluid", FluidCondition.CODEC);
		register("movement_blocking", SimpleBlockConditions.MOVEMENT_BLOCKING);
		register("light_blocking", SimpleBlockConditions.LIGHT_BLOCKING);
		register("water_loggable", SimpleBlockConditions.WATER_LOGGABLE);
		register("exposed_to_sky", SimpleBlockConditions.EXPOSED_TO_SKY);
		register("light_level", LightLevelCondition.CODEC);
		register("block_state", BlockStateCondition.CODEC);
	}

	private static void register(String name, Codec<? extends Predicate<CachedBlockPosition>> codec) {
		ModRegistriesArchitectury.BLOCK_CONDITION.registerSupplied(Origins.identifier(name), () -> new ConditionFactory<>(codec));
	}
}
