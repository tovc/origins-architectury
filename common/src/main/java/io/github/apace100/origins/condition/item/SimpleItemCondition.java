package io.github.apace100.origins.condition.item;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.configuration.NoConfiguration;
import io.github.apace100.origins.api.power.factory.ItemCondition;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class SimpleItemCondition extends ItemCondition<NoConfiguration> {
	private final Predicate<ItemStack> predicate;

	public SimpleItemCondition(Predicate<ItemStack> predicate) {
		super(NoConfiguration.CODEC);
		this.predicate = predicate;
	}

	@Override
	public boolean check(NoConfiguration configuration, ItemStack stack) {
		return this.predicate.test(stack);
	}
}
