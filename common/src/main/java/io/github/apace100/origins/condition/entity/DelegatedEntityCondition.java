package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.condition.meta.IDelegatedConditionConfiguration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.apache.commons.lang3.tuple.Pair;

public class DelegatedEntityCondition<T extends IDelegatedConditionConfiguration<LivingEntity>> extends EntityCondition<T> {
	public DelegatedEntityCondition(Codec<T> codec) {
		super(codec);
	}

	@Override
	public boolean check(T configuration, LivingEntity entity) {
		return configuration.check(entity);
	}
}
