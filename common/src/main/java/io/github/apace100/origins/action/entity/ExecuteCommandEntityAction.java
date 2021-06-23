package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.action.configuration.CommandConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;

public class ExecuteCommandEntityAction extends EntityAction<CommandConfiguration> {

	public ExecuteCommandEntityAction() {
		super(CommandConfiguration.CODEC);
	}

	@Override
	public void execute(CommandConfiguration configuration, Entity entity) {
		configuration.execute(entity);
	}
}
