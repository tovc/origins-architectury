package io.github.apace100.origins.condition.block;

import io.github.apace100.origins.action.configuration.OffsetConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import io.github.apace100.origins.api.power.factory.BlockCondition;
import net.minecraft.block.pattern.CachedBlockPosition;

public class OffsetCondition extends BlockCondition<OffsetConfiguration<ConfiguredBlockCondition<?, ?>>> {

	public OffsetCondition() {
		super(OffsetConfiguration.codec("condition", ConfiguredBlockCondition.CODEC));
	}

	@Override
	protected boolean check(OffsetConfiguration<ConfiguredBlockCondition<?, ?>> configuration, CachedBlockPosition block) {
		return configuration.value().check(new CachedBlockPosition(block.getWorld(), block.getBlockPos().add(configuration.asBlockPos()), true));
	}
}
