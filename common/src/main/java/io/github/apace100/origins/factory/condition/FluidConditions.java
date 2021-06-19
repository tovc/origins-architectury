package io.github.apace100.origins.factory.condition;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.factory.MetaFactories;
import io.github.apace100.origins.condition.fluid.InTagCondition;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.fluid.FluidState;

import java.util.function.Predicate;

public class FluidConditions {

	public static void register() {
		MetaFactories.defineMetaConditions(ModRegistriesArchitectury.FLUID_CONDITION, OriginsCodecs.FLUID_CONDITION);
		register("empty", FluidState::isEmpty);
		register("still", FluidState::isStill);
		register("in_tag", InTagCondition.CODEC);
	}

	private static void register(String name, Codec<? extends Predicate<FluidState>> codec) {
		ModRegistriesArchitectury.FLUID_CONDITION.registerSupplied(Origins.identifier(name), () -> new ConditionFactory<>(codec));
	}

	private static void register(String name, Predicate<FluidState> predicate) {
		register(name, Codec.unit(predicate));
	}
}
