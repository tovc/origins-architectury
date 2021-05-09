package io.github.apace100.origins.power.condition.block;

import com.mojang.serialization.Codec;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.pattern.CachedBlockPosition;

import java.util.function.Predicate;

public class SimpleBlockConditions {
	public static final Codec<Predicate<CachedBlockPosition>> REPLACEABLE = Codec.unit(t -> t.getBlockState().getMaterial().isReplaceable());
	public static final Codec<Predicate<CachedBlockPosition>> MOVEMENT_BLOCKING = Codec.unit(t -> t.getBlockState().getMaterial().blocksMovement() && !t.getBlockState().getCollisionShape(t.getWorld(), t.getBlockPos()).isEmpty());
	public static final Codec<Predicate<CachedBlockPosition>> LIGHT_BLOCKING = Codec.unit(t -> t.getBlockState().getMaterial().blocksLight());
	public static final Codec<Predicate<CachedBlockPosition>> WATER_LOGGABLE = Codec.unit(t -> t.getBlockState().getBlock() instanceof FluidFillable);
	public static final Codec<Predicate<CachedBlockPosition>> EXPOSED_TO_SKY = Codec.unit(t -> t.getWorld().isSkyVisible(t.getBlockPos()));
}
