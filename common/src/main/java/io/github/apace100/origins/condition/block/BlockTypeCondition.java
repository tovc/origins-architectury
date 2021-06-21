package io.github.apace100.origins.condition.block;

import io.github.apace100.origins.action.configuration.BlockConfiguration;
import io.github.apace100.origins.api.power.factory.BlockCondition;
import net.minecraft.block.pattern.CachedBlockPosition;

public class BlockTypeCondition extends BlockCondition<BlockConfiguration> {

	public BlockTypeCondition() {
		super(BlockConfiguration.codec("block"));
	}

	@Override
	protected boolean check(BlockConfiguration configuration, CachedBlockPosition block) {
		return block.getBlockState().isOf(configuration.block());
	}
}
