package io.github.apace100.origins.condition.item;

import io.github.apace100.origins.api.power.factory.ItemCondition;
import io.github.apace100.origins.condition.configuration.EnchantmentConfiguration;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class EnchantmentCondition extends ItemCondition<EnchantmentConfiguration> {

	public EnchantmentCondition() {
		super(EnchantmentConfiguration.CODEC);
	}

	@Override
	public boolean check(EnchantmentConfiguration configuration, ItemStack stack) {
		return configuration.applyCheck(stack);
	}
}
