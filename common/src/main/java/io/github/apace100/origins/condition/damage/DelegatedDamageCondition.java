package io.github.apace100.origins.condition.damage;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.factory.BlockCondition;
import io.github.apace100.origins.api.power.factory.DamageCondition;
import io.github.apace100.origins.condition.meta.IDelegatedConditionConfiguration;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.damage.DamageSource;
import org.apache.commons.lang3.tuple.Pair;

public class DelegatedDamageCondition<T extends IDelegatedConditionConfiguration<Pair<DamageSource, Float>>> extends DamageCondition<T> {
	public DelegatedDamageCondition(Codec<T> codec) {
		super(codec);
	}

	@Override
	protected boolean check(T configuration, DamageSource source, float amount) {
		return configuration.check(Pair.of(source, amount));
	}
}
