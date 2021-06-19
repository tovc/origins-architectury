package io.github.apace100.origins.condition.damage;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;

import java.util.function.Predicate;

@FunctionalInterface
public interface DamageCondition extends Predicate<Pair<DamageSource, Float>> {
	boolean test(DamageSource source, float f);

	@Override
	default boolean test(Pair<DamageSource, Float> pair) {
		return this.test(pair.getLeft(), pair.getRight());
	}
}
