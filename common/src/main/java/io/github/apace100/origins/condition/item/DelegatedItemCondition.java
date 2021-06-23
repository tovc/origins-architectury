package io.github.apace100.origins.condition.item;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.factory.ItemCondition;
import io.github.apace100.origins.condition.meta.IDelegatedConditionConfiguration;
import net.minecraft.item.ItemStack;

public class DelegatedItemCondition<T extends IDelegatedConditionConfiguration<ItemStack>> extends ItemCondition<T> {
	public DelegatedItemCondition(Codec<T> codec) {
		super(codec);
	}


	@Override
	public boolean check(T configuration, ItemStack stack) {
		return configuration.check(stack);
	}
}
