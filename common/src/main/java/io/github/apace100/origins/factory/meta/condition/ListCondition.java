package io.github.apace100.origins.factory.meta.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.factory.condition.ConditionFactory;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ListCondition<T> implements Predicate<T> {

	public static <T> Codec<ListCondition<T>> codec(Codec<List<ConditionFactory.Instance<T>>> codec, BiPredicate<T, List<? extends Predicate<T>>> predicate) {
		return RecordCodecBuilder.create(instance -> instance.group(
				codec.fieldOf("conditions").forGetter(x -> x.instances)
		).apply(instance, c -> new ListCondition<>(c, predicate)));
	}

	private final List<ConditionFactory.Instance<T>> instances;
	private final BiPredicate<T, List<? extends Predicate<T>>> predicate;

	public ListCondition(List<ConditionFactory.Instance<T>> instances, BiPredicate<T, List<? extends Predicate<T>>> predicate) {
		this.instances = instances;
		this.predicate = predicate;
	}

	@Override
	public boolean test(T t) {
		return this.predicate.test(t, this.instances);
	}
}
