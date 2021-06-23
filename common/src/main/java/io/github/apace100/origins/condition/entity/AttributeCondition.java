package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.condition.configuration.AttributeComparisonConfiguration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;

public class AttributeCondition extends EntityCondition<AttributeComparisonConfiguration> {

	public AttributeCondition() {
		super(AttributeComparisonConfiguration.CODEC);
	}

	@Override
	public boolean check(AttributeComparisonConfiguration configuration, LivingEntity entity) {
		if (configuration.attribute() == null)
			return false;
		EntityAttributeInstance attributeInstance = entity.getAttributeInstance(configuration.attribute());
		return configuration.comparison().check(attributeInstance != null ? attributeInstance.getValue() : 0);
	}
}

