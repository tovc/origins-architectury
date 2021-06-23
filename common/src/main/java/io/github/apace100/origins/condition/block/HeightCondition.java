package io.github.apace100.origins.condition.block;

import io.github.apace100.origins.api.configuration.IntegerComparisonConfiguration;
import io.github.apace100.origins.api.power.factory.BlockCondition;
import net.minecraft.block.pattern.CachedBlockPosition;

public class HeightCondition extends BlockCondition<IntegerComparisonConfiguration> {
	public HeightCondition() {
		super(IntegerComparisonConfiguration.CODEC);
	}

	@Override
	protected boolean check(IntegerComparisonConfiguration configuration, CachedBlockPosition block) {
		return configuration.check(block.getBlockPos().getY());
	}
}
