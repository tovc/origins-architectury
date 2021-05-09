package io.github.apace100.origins.power.condition.block;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.power.condition.ConditionCodecs;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

import java.util.Arrays;
import java.util.function.Predicate;

public class AttachableCondition implements Predicate<CachedBlockPosition> {

	public static final Codec<AttachableCondition> CODEC = Codec.unit(new AttachableCondition());

	@Override
	public boolean test(CachedBlockPosition blockPosition) {
		WorldView world = blockPosition.getWorld();
		BlockPos pos = blockPosition.getBlockPos();
		return Arrays.stream(Direction.values()).anyMatch(d -> world.getBlockState(pos.offset(d)).isSideSolidFullSquare(world, pos, d.getOpposite()));
	}
}
