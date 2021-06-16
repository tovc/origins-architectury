package io.github.apace100.origins.power.factory.meta.action;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.power.IFactory;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class IfElseListAction<T, V> implements Consumer<T> {
	public static <T, V>Codec<IfElseListAction<T, V>> codec(Codec<IFactory.Instance<T>> action, Codec<ConditionFactory.Instance<V>> condition, Function<T, V> converter) {
		Codec<List<Pair<IFactory.Instance<T>, ConditionFactory.Instance<V>>>> codec = RecordCodecBuilder.<Pair<IFactory.Instance<T>, ConditionFactory.Instance<V>>>create(instance -> instance.group(
				action.fieldOf("action").forGetter(Pair::getFirst),
				condition.fieldOf("condition").forGetter(Pair::getSecond)
		).apply(instance, Pair::of)).listOf();
		return RecordCodecBuilder.create(instance -> instance.group(
				codec.fieldOf("actions").forGetter(x -> x.actions)
		).apply(instance, pairs -> new IfElseListAction<>(pairs, converter)));
	}

	private final List<Pair<IFactory.Instance<T>, ConditionFactory.Instance<V>>> actions;
	private final Function<T, V> converter;

	public IfElseListAction(List<Pair<IFactory.Instance<T>, ConditionFactory.Instance<V>>> actions, Function<T, V> converter) {
		this.actions = actions;
		this.converter = converter;
	}

	@Override
	public void accept(T t) {
		V apply = this.converter.apply(t);
		if (apply == null) return;
		for (Pair<IFactory.Instance<T>, ConditionFactory.Instance<V>> action : this.actions) {
			if (action.getSecond().test(apply))
				action.getFirst().accept(t);
		}
	}
}
