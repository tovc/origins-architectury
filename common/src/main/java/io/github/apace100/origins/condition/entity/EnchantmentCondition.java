package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.condition.configuration.EnchantmentConfiguration;
import net.minecraft.entity.LivingEntity;

public class EnchantmentCondition extends EntityCondition<EnchantmentConfiguration> {

	public EnchantmentCondition() {
		super(EnchantmentConfiguration.CODEC);
	}

	@Override
	public boolean check(EnchantmentConfiguration configuration, LivingEntity entity) {
		return configuration.enchantment() != null && configuration.applyCheck(configuration.enchantment().getEquipment(entity).values());
	}
}
