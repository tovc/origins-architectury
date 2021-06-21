package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.configuration.IntegerComparisonConfiguration;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import net.minecraft.entity.LivingEntity;

import java.util.function.Function;

public class IntComparingEntityCondition extends EntityCondition<IntegerComparisonConfiguration> {
	private final Function<LivingEntity, Integer> function;

	public IntComparingEntityCondition(Function<LivingEntity, Integer> function) {
		super(IntegerComparisonConfiguration.CODEC);
		this.function = function;
	}

	@Override
	public boolean check(IntegerComparisonConfiguration configuration, LivingEntity entity) {
		Integer apply = function.apply(entity);
		return apply != null && configuration.check(apply);
	}
}
