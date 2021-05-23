package io.github.apace100.origins.power.action.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;

public class ConsumeAction implements Consumer<ItemStack> {

	public static Codec<ConsumeAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("amount", 1).forGetter(x -> x.amount)
	).apply(instance, ConsumeAction::new));

	private final int amount;

	public ConsumeAction(int amount) {this.amount = amount;}

	@Override
	public void accept(ItemStack itemStack) {
		itemStack.decrement(this.amount);
	}
}
