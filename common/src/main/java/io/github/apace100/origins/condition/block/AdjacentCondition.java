package io.github.apace100.origins.condition.block;

import io.github.apace100.origins.api.power.factory.BlockCondition;
import io.github.apace100.origins.condition.configuration.AdjacentConfiguration;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

public class AdjacentCondition extends BlockCondition<AdjacentConfiguration> {
	public AdjacentCondition() {
		super(AdjacentConfiguration.CODEC);
	}

	@Override
	protected boolean check(AdjacentConfiguration configuration, CachedBlockPosition block) {
		int count = Math.toIntExact(Arrays.stream(Direction.values())
				.map(x -> new CachedBlockPosition(block.getWorld(), block.getBlockPos().offset(x), true))
				.filter(configuration.condition()::check).count());
		return configuration.comparison().check(count);
	}
}
