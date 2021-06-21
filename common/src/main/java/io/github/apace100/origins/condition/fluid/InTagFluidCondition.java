package io.github.apace100.origins.condition.fluid;

import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.FluidCondition;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.Tag;

public class InTagFluidCondition extends FluidCondition<FieldConfiguration<Tag<Fluid>>> {

	public InTagFluidCondition() {
		super(FieldConfiguration.codec(OriginsCodecs.FLUID_TAG, "tag"));
	}

	@Override
	public boolean check(FieldConfiguration<Tag<Fluid>> configuration, FluidState fluid) {
		return fluid.isIn(configuration.value());
	}
}
