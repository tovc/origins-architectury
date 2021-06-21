package io.github.apace100.origins.registry.action;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.action.item.ConsumeAction;
import io.github.apace100.origins.action.item.DelegatedItemAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemCondition;
import io.github.apace100.origins.api.power.factory.ItemAction;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import io.github.apace100.origins.factory.MetaFactories;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.item.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class ModItemActions {
	public static final BiConsumer<ConfiguredItemAction<?, ?>, ItemStack> EXECUTOR = (action, stack) -> action.execute(stack);
	public static final BiPredicate<ConfiguredItemCondition<?, ?>, ItemStack> PREDICATE = (condition, stack) -> condition.check(stack);

	public static final RegistrySupplier<ConsumeAction> CONSUME = register("consume", ConsumeAction::new);

	public static void register() {
		MetaFactories.defineMetaActions(OriginsRegistries.ITEM_ACTION, DelegatedItemAction::new, ConfiguredItemAction.CODEC, ConfiguredItemCondition.CODEC, EXECUTOR, PREDICATE);
	}

	@SuppressWarnings("unchecked")
	private static <T extends ItemAction<?>> RegistrySupplier<T> register(String name, Supplier<T> factory) {
		return (RegistrySupplier<T>) OriginsRegistries.ITEM_ACTION.registerSupplied(Origins.identifier(name), factory::get);
	}
}
