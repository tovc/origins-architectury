package io.github.apace100.origins.action.item;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.ItemAction;
import net.minecraft.item.ItemStack;

public class ConsumeAction extends ItemAction<FieldConfiguration<Integer>> {

	public ConsumeAction() {super(FieldConfiguration.codec(Codec.INT, "amount", 1));}

	@Override
	public void execute(FieldConfiguration<Integer> configuration, ItemStack stack) {
		stack.decrement(configuration.value());
	}
}
