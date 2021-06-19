package io.github.apace100.origins.factory.meta.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;

import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public class IntComparingCondition<T> implements Predicate<T> {
	public static <T> Codec<IntComparingCondition<T>> codec(ToIntFunction<T> func) {
		return RecordCodecBuilder.create(instance -> instance.group(
				OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(x -> x.comparison),
				Codec.INT.fieldOf("compare_to").forGetter(x -> x.compareTo)
		).apply(instance, (comparison1, aFloat) -> new IntComparingCondition<>(comparison1, aFloat, func)));
	}

	private final Comparison comparison;
	private final int compareTo;
	private final ToIntFunction<T> function;

	public IntComparingCondition(Comparison comparison, int compareTo, ToIntFunction<T> function) {
		this.comparison = comparison;
		this.compareTo = compareTo;
		this.function = function;
	}
	@Override
	public boolean test(T t) {
		int i = function.applyAsInt(t);
		return i != Integer.MIN_VALUE && comparison.compare(i, compareTo);
	}
}
