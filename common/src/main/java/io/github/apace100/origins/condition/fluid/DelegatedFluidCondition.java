package io.github.apace100.origins.condition.fluid;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.factory.FluidCondition;
import io.github.apace100.origins.condition.meta.IDelegatedConditionConfiguration;
import net.minecraft.fluid.FluidState;

public class DelegatedFluidCondition<T extends IDelegatedConditionConfiguration<FluidState>> extends FluidCondition<T> {
	public DelegatedFluidCondition(Codec<T> codec) {
		super(codec);
	}


	@Override
	public boolean check(T configuration, FluidState fluid) {
		return configuration.check(fluid);
	}
}
