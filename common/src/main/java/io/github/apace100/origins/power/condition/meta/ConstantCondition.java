package io.github.apace100.origins.power.condition.meta;

import com.mojang.serialization.Codec;

import java.util.function.Predicate;

public class ConstantCondition<T> implements Predicate<T> {

	public static <T> Codec<ConstantCondition<T>> codec() {
		return Codec.BOOL.xmap(ConstantCondition::new, x -> x.value);
	}

	private final boolean value;

	public ConstantCondition(boolean value) {
		this.value = value;
	}

	@Override
	public boolean test(T t) {
		return false;
	}
}
