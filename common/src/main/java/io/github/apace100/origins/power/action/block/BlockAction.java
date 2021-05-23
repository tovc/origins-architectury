package io.github.apace100.origins.power.action.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.Consumer;

public interface BlockAction extends Consumer<Triple<World, BlockPos, Direction>> {
	@Override
	default void accept(Triple<World, BlockPos, Direction> triple) {
		this.accept(triple.getLeft(), triple.getMiddle(), triple.getRight());
	}

	void accept(World world, BlockPos pos, Direction direction);
}
