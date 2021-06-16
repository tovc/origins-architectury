package io.github.apace100.origins.power.action.block;

import io.github.apace100.origins.power.configuration.BlockConfiguration;
import io.github.apace100.origins.api.power.factory.BlockAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SetBlockAction extends BlockAction<BlockConfiguration> {

	public SetBlockAction() {
		super(BlockConfiguration.codec("block"));
	}

	@Override
	public void execute(BlockConfiguration configuration, World world, BlockPos pos, Direction direction) {
		configuration.getBlock().ifPresent(b -> world.setBlockState(pos, b.getDefaultState()));
	}
}
