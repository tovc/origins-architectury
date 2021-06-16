package io.github.apace100.origins.registry;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import io.github.apace100.origins.power.factories.*;
import me.shedaniel.architectury.registry.RegistrySupplier;

import java.util.function.Supplier;

public class ModPowers {
	public static void initialize() { }

	public static final RegistrySupplier<ActionOnBlockBreakPower> ACTION_ON_BLOCK_BREAK = register("action_on_block_break", ActionOnBlockBreakPower::new);
	public static final RegistrySupplier<ActionOnCallbackPower> ACTION_ON_CALLBACK = register("action_on_callback", ActionOnCallbackPower::new);
	public static final RegistrySupplier<ActionOnItemUsePower> ACTION_ON_ITEM_USE = register("action_on_item_use", ActionOnItemUsePower::new);
	public static final RegistrySupplier<ActionOnLandPower> ACTION_ON_LAND = register("action_on_land", ActionOnLandPower::new);
	public static final RegistrySupplier<ActionOnWakeUpPower> ACTION_ON_WAKE_UP = register("action_on_wake_up", ActionOnWakeUpPower::new);
	public static final RegistrySupplier<ActionOverTimePower> ACTION_OVER_TIME = register("action_over_time", ActionOverTimePower::new);

	@SuppressWarnings("unchecked")
	private static <T extends PowerFactory<?>> RegistrySupplier<T> register(String name, Supplier<T> factory) {
		return (RegistrySupplier<T>) OriginsRegistries.POWER_FACTORY.registerSupplied(Origins.identifier(name), factory::get);
	}
}
