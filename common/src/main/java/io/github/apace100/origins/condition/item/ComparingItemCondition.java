package io.github.apace100.origins.condition.item;

import io.github.apace100.origins.api.configuration.IntegerComparisonConfiguration;
import io.github.apace100.origins.api.power.factory.ItemCondition;
import net.minecraft.item.ItemStack;

import java.util.function.ToIntFunction;

public class ComparingItemCondition extends ItemCondition<IntegerComparisonConfiguration> {
	private final ToIntFunction<ItemStack> function;

	public ComparingItemCondition(ToIntFunction<ItemStack> function) {
		super(IntegerComparisonConfiguration.CODEC);
		this.function = function;
	}

	@Override
	public boolean check(IntegerComparisonConfiguration configuration, ItemStack stack) {
		return configuration.check(this.function.applyAsInt(stack));
	}
}
