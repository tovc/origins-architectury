package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.action.configuration.DamageConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class DamageAction extends EntityAction<DamageConfiguration> {

	public DamageAction() {
		super(DamageConfiguration.CODEC);
	}

	@Override
	public void execute(DamageConfiguration configuration, Entity entity) {
		if (entity instanceof LivingEntity)
			entity.damage(configuration.source(), configuration.amount());
	}
}
