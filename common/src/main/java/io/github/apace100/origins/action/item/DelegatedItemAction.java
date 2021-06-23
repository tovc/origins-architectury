package io.github.apace100.origins.action.item;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.action.meta.IDelegatedActionConfiguration;
import io.github.apace100.origins.api.power.factory.ItemAction;
import net.minecraft.item.ItemStack;

public class DelegatedItemAction<T extends IDelegatedActionConfiguration<ItemStack>> extends ItemAction<T> {
	public DelegatedItemAction(Codec<T> codec) {
		super(codec);
	}

	@Override
	public void execute(T configuration, ItemStack stack) {
		configuration.execute(stack);
	}
}
