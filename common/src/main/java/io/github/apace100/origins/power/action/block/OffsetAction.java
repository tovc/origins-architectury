package io.github.apace100.origins.power.action.block;

import io.github.apace100.origins.power.configuration.OffsetConfiguration;
import io.github.apace100.origins.api.power.factory.BlockAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockAction;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class OffsetAction extends BlockAction<OffsetConfiguration<ConfiguredBlockAction<?, ?>>> {

	protected OffsetAction() {
		super(OffsetConfiguration.codec("action", ConfiguredBlockAction.CODEC));
	}

	@Override
	public void execute(OffsetConfiguration<ConfiguredBlockAction<?, ?>> configuration, World world, BlockPos pos, Direction direction) {
		configuration.value().execute(world, pos.add(configuration.asBlockPos()), direction);
	}
}
