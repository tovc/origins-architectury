package io.github.apace100.origins.power.condition.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;

import java.util.function.Function;
import java.util.function.Predicate;

public class FloatComparingCondition<T> implements Predicate<T> {
	public static <T> Codec<FloatComparingCondition<T>> codec(Function<T, Float> func) {
		return RecordCodecBuilder.create(instance -> instance.group(
				OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(x -> x.comparison),
				Codec.FLOAT.fieldOf("compare_to").forGetter(x -> x.compareTo)
		).apply(instance, (comparison1, aFloat) -> new FloatComparingCondition<>(comparison1, aFloat, func)));
	}

	private final Comparison comparison;
	private final float compareTo;
	private final Function<T, Float> function;

	public FloatComparingCondition(Comparison comparison, float compareTo, Function<T, Float> function) {
		this.comparison = comparison;
		this.compareTo = compareTo;
		this.function = function;
	}
	@Override
	public boolean test(T t) {
		return comparison.compare(function.apply(t), compareTo);
	}
}
