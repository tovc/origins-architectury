package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.action.configuration.EquippedItemConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class EquippedItemAction extends EntityAction<EquippedItemConfiguration> {

	public EquippedItemAction() {
		super(EquippedItemConfiguration.CODEC);
	}

	@Override
	public void execute(EquippedItemConfiguration configuration, Entity entity) {
		if (entity instanceof LivingEntity living)
			configuration.action().execute(living.getEquippedStack(configuration.slot()));
	}
}
