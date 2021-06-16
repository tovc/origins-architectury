package io.github.apace100.origins.power.factory.meta.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.power.IFactory;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class IfElseAction<T, V> implements Consumer<T> {
	public static <T, V> Codec<IfElseAction<T, V>> codec(Codec<IFactory.Instance<T>> first, Codec<ConditionFactory.Instance<V>> sec, Function<T, V> trans) {
		return RecordCodecBuilder.create(instance -> instance.group(
				sec.fieldOf("condition").forGetter(x -> x.condition),
				first.fieldOf("if_action").forGetter(x -> x.ifAction),
				first.optionalFieldOf("else_action").forGetter(x -> x.elseAction)
		).apply(instance, (c, i, e) -> new IfElseAction<>(c, i, e, trans)));
	}

	private final ConditionFactory.Instance<V> condition;
	private final IFactory.Instance<T> ifAction;
	private final Optional<IFactory.Instance<T>> elseAction;
	private final Function<T, V> converter;

	public IfElseAction(ConditionFactory.Instance<V> condition, IFactory.Instance<T> ifAction, Optional<IFactory.Instance<T>> elseAction, Function<T, V> converter) {
		this.condition = condition;
		this.ifAction = ifAction;
		this.elseAction = elseAction;
		this.converter = converter;
	}

	@Override
	public void accept(T t) {
		V apply = this.converter.apply(t);
		if (apply == null) return;
		if (this.condition.test(apply))
			ifAction.accept(t);
		else
			elseAction.ifPresent(x -> x.accept(t));
	}
}
