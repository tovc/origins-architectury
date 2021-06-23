package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.condition.configuration.FluidTagComparisonConfiguration;
import net.minecraft.entity.LivingEntity;

public class FluidHeightCondition extends EntityCondition<FluidTagComparisonConfiguration> {

	public FluidHeightCondition() {
		super(FluidTagComparisonConfiguration.CODEC);
	}

	@Override
	public boolean check(FluidTagComparisonConfiguration configuration, LivingEntity entity) {
		return configuration.comparison().check(entity.getFluidHeight(configuration.tag()));
	}
}
