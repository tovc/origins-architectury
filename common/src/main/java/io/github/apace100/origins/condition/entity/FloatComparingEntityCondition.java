package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.configuration.FloatComparisonConfiguration;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import net.minecraft.entity.LivingEntity;

import java.util.function.Function;

public class FloatComparingEntityCondition extends EntityCondition<FloatComparisonConfiguration> {
	private final Function<LivingEntity, Float> function;

	public FloatComparingEntityCondition(Function<LivingEntity, Float> function) {
		super(FloatComparisonConfiguration.CODEC);
		this.function = function;
	}

	@Override
	public boolean check(FloatComparisonConfiguration configuration, LivingEntity entity) {
		Float apply = function.apply(entity);
		return apply != null && configuration.check(apply);
	}
}
