package io.github.apace100.origins.registry.condition;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.configuration.ConfiguredFluidCondition;
import io.github.apace100.origins.api.power.factory.FluidCondition;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import io.github.apace100.origins.condition.fluid.DelegatedFluidCondition;
import io.github.apace100.origins.condition.fluid.InTagFluidCondition;
import io.github.apace100.origins.condition.fluid.SimpleFluidCondition;
import io.github.apace100.origins.factory.MetaFactories;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.fluid.FluidState;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class ModFluidConditions {
	public static final BiPredicate<ConfiguredFluidCondition<?, ?>, FluidState> PREDICATE = (config, biome) -> config.check(biome);

	public static void register() {
		MetaFactories.defineMetaConditions(OriginsRegistries.FLUID_CONDITION, DelegatedFluidCondition::new, ConfiguredFluidCondition.CODEC, PREDICATE);
	}

	@SuppressWarnings("unchecked")
	private static <T extends FluidCondition<?>> RegistrySupplier<T> register(String name, Supplier<T> factory) {
		return (RegistrySupplier<T>) OriginsRegistries.FLUID_CONDITION.registerSupplied(Origins.identifier(name), factory::get);
	}

	public static RegistrySupplier<SimpleFluidCondition> EMPTY = register("empty", () -> new SimpleFluidCondition(FluidState::isEmpty));
	public static RegistrySupplier<SimpleFluidCondition> STILL = register("still", () -> new SimpleFluidCondition(FluidState::isStill));
	public static RegistrySupplier<InTagFluidCondition> IN_TAG = register("in_tag", InTagFluidCondition::new);
}
