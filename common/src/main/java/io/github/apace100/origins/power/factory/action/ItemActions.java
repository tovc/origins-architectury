package io.github.apace100.origins.power.factory.action;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.action.item.ConsumeAction;
import io.github.apace100.origins.power.factory.MetaFactories;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class ItemActions {

	public static void register() {
		MetaFactories.defineMetaActions(ModRegistriesArchitectury.ITEM_ACTION, OriginsCodecs.ITEM_ACTION, OriginsCodecs.ITEM_CONDITION, Function.identity());
		register("consume", ConsumeAction.CODEC);
	}

	private static void register(String name, Codec<? extends Consumer<ItemStack>> codec) {
		ModRegistriesArchitectury.ITEM_ACTION.register(Origins.identifier(name), () -> new ActionFactory<>(codec));
	}
}
