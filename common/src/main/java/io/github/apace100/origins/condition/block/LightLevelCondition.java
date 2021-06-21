package io.github.apace100.origins.condition.block;

import io.github.apace100.origins.api.power.factory.BlockCondition;
import io.github.apace100.origins.condition.configuration.LightLevelConfiguration;
import net.minecraft.block.pattern.CachedBlockPosition;

public class LightLevelCondition extends BlockCondition<LightLevelConfiguration> {

	public LightLevelCondition() {
		super(LightLevelConfiguration.CODEC);
	}

	@Override
	protected boolean check(LightLevelConfiguration configuration, CachedBlockPosition block) {
		return configuration.comparison().check(configuration.getLightLevel(block.getWorld(), block.getBlockPos()));
	}
}
