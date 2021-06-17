package io.github.apace100.origins.api.power.factory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.IConditionFactory;
import io.github.apace100.origins.api.power.configuration.ConfiguredFluidCondition;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import me.shedaniel.architectury.core.RegistryEntry;
import net.minecraft.fluid.FluidState;

public abstract class FluidCondition<T extends IOriginsFeatureConfiguration> extends RegistryEntry<FluidCondition<?>> implements IConditionFactory<T, ConfiguredFluidCondition<T, ?>, FluidCondition<T>> {
	public static final Codec<FluidCondition<?>> CODEC = OriginsRegistries.codec(OriginsRegistries.FLUID_CONDITION);
	private final Codec<Pair<T, ConditionData>> codec;

	protected FluidCondition(Codec<T> codec) {
		this.codec = IConditionFactory.conditionCodec(codec);
	}

	@Override
	public Codec<Pair<T, ConditionData>> getConditionCodec() {
		return this.codec;
	}

	@Override
	public final ConfiguredFluidCondition<T, ?> configure(T input, ConditionData data) {
		return new ConfiguredFluidCondition<>(this, input, data);
	}

	public boolean check(T configuration, FluidState fluid) {
		return false;
	}

	public boolean check(T configuration, ConditionData data, FluidState fluid) {
		return data.inverted() ^ this.check(configuration, fluid);
	}
}
