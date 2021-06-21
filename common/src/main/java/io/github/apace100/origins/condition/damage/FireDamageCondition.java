package io.github.apace100.origins.condition.damage;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.configuration.NoConfiguration;
import io.github.apace100.origins.api.power.factory.DamageCondition;
import net.minecraft.entity.damage.DamageSource;

public class FireDamageCondition extends DamageCondition<NoConfiguration> {
	public FireDamageCondition() {
		super(NoConfiguration.CODEC);
	}

	@Override
	protected boolean check(NoConfiguration configuration, DamageSource source, float amount) {
		return source.isFire();
	}
}
