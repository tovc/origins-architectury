package io.github.apace100.origins.condition.block;

import io.github.apace100.origins.api.configuration.NoConfiguration;
import io.github.apace100.origins.api.power.factory.BlockCondition;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.pattern.CachedBlockPosition;

import java.util.function.Predicate;

public class SimpleBlockCondition extends BlockCondition<NoConfiguration> {

	public static final Predicate<CachedBlockPosition> REPLACEABLE = t -> t.getBlockState().getMaterial().isReplaceable();
	public static final Predicate<CachedBlockPosition> MOVEMENT_BLOCKING = t -> t.getBlockState().getMaterial().blocksMovement() && !t.getBlockState().getCollisionShape(t.getWorld(), t.getBlockPos()).isEmpty();
	public static final Predicate<CachedBlockPosition> LIGHT_BLOCKING = t -> t.getBlockState().getMaterial().blocksLight();
	public static final Predicate<CachedBlockPosition> WATER_LOGGABLE = t -> t.getBlockState().getBlock() instanceof FluidFillable;
	public static final Predicate<CachedBlockPosition> EXPOSED_TO_SKY = t -> t.getWorld().isSkyVisible(t.getBlockPos());

	private final Predicate<CachedBlockPosition> predicate;

	public SimpleBlockCondition(Predicate<CachedBlockPosition> predicate) {
		super(NoConfiguration.CODEC);
		this.predicate = predicate;
	}

	@Override
	public boolean check(NoConfiguration configuration, CachedBlockPosition block) {
		return this.predicate.test(block);
	}
}
