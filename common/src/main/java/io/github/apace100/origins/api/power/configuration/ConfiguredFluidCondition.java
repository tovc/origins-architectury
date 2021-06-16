package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.ConfiguredCondition;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.api.power.factory.FluidCondition;
import net.minecraft.fluid.FluidState;

import java.util.function.Function;

public final class ConfiguredFluidCondition<T extends IOriginsFeatureConfiguration> extends ConfiguredCondition<T, FluidCondition<T>> {
	public static final Codec<ConfiguredFluidCondition<?>> CODEC = FluidCondition.CODEC.dispatch(ConfiguredFactory::getFactory, Function.identity());

	public ConfiguredFluidCondition(FluidCondition<T> factory, T configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(FluidState fluid) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), fluid);
	}
}