package io.github.apace100.origins.condition.damage;

import io.github.apace100.origins.api.configuration.FloatComparisonConfiguration;
import io.github.apace100.origins.api.power.factory.DamageCondition;
import net.minecraft.entity.damage.DamageSource;

public class AmountCondition extends DamageCondition<FloatComparisonConfiguration> {

	public AmountCondition() {
		super(FloatComparisonConfiguration.CODEC);
	}

	@Override
	protected boolean check(FloatComparisonConfiguration configuration, DamageSource source, float amount) {
		return configuration.check(amount);
	}
}
