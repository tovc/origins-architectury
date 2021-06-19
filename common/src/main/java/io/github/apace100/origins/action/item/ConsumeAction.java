package io.github.apace100.origins.action.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.power.configuration.power.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.ItemAction;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;

public class ConsumeAction extends ItemAction<FieldConfiguration<Integer>> {

	public ConsumeAction() {super(FieldConfiguration.codec(Codec.INT, "amount", 1));}

	@Override
	public void execute(FieldConfiguration<Integer> configuration, ItemStack stack) {
		stack.decrement(configuration.value());
	}
}
