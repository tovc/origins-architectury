package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.PhasingConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class PhasingPower extends PowerFactory<PhasingConfiguration> {
	public static boolean shouldPhaseThrough(LivingEntity entity, CachedBlockPosition position, boolean isAbove) {
		return OriginComponent.getPowers(entity, ModPowers.PHASING.get()).stream().map(x -> (!isAbove || x.getConfiguration().canPhaseDown(entity)) && x.getConfiguration().canPhaseThrough(position)).anyMatch(Boolean::booleanValue);
	}

	public static boolean shouldPhaseThrough(LivingEntity entity, CachedBlockPosition position) {
		return shouldPhaseThrough(entity, position, false);
	}

	public static boolean shouldPhaseThrough(LivingEntity entity, BlockPos pos) {
		return shouldPhaseThrough(entity, new CachedBlockPosition(entity.world, pos, true), false);
	}

	public static boolean hasRenderMethod(Entity entity, PhasingConfiguration.RenderType renderType) {
		return OriginComponent.getPowers(entity, ModPowers.PHASING.get()).stream().anyMatch(x -> renderType.equals(x.getConfiguration().renderType()));
	}

	public PhasingPower() {
		super(PhasingConfiguration.CODEC);
	}
}
