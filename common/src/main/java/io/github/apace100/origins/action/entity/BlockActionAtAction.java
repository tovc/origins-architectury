package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockAction;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;

public class BlockActionAtAction extends EntityAction<FieldConfiguration<ConfiguredBlockAction<?, ?>>> {

	public BlockActionAtAction() {
		super(FieldConfiguration.codec(ConfiguredBlockAction.CODEC, "block_action"));
	}

	@Override
	public void execute(FieldConfiguration<ConfiguredBlockAction<?, ?>> configuration, Entity entity) {
		configuration.value().execute(entity.world, entity.getBlockPos(), Direction.UP);
	}
}
