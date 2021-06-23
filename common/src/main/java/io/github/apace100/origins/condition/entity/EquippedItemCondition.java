package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.condition.configuration.EquippedItemConfiguration;
import net.minecraft.entity.LivingEntity;

public class EquippedItemCondition extends EntityCondition<EquippedItemConfiguration> {
	public EquippedItemCondition() {
		super(EquippedItemConfiguration.CODEC);
	}

	@Override
	public boolean check(EquippedItemConfiguration configuration, LivingEntity entity) {
		return configuration.condition().check(entity.getEquippedStack(configuration.slot()));
	}
}
