package io.github.apace100.origins.power.factory;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.power.factory.meta.action.*;
import io.github.apace100.origins.power.factory.meta.condition.ConstantCondition;
import io.github.apace100.origins.power.factory.meta.condition.ListCondition;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.FilterableWeightedList;
import io.github.apace100.origins.util.OriginsCodecs;
import me.shedaniel.architectury.registry.Registry;

import java.util.List;
import java.util.function.Function;

public class MetaFactories {
	public static <T> void defineMetaConditions(Registry<ConditionFactory<T>> registry, Codec<ConditionFactory.Instance<T>> codec) {
		Codec<List<ConditionFactory.Instance<T>>> listCodec = OriginsCodecs.listOf(codec);
		registry.register(Origins.identifier("constant"), () -> new ConditionFactory<>(ConstantCondition.codec()));
		registry.register(Origins.identifier("and"), () -> new ConditionFactory<>(ListCondition.codec(listCodec, (t, ls) -> ls.stream().allMatch(x -> x.test(t)))));
		registry.register(Origins.identifier("or"), () -> new ConditionFactory<>(ListCondition.codec(listCodec, (t, ls) -> ls.stream().anyMatch(x -> x.test(t)))));
	}

	public static <T, V> void defineMetaActions(Registry<ActionFactory<T>> registry, Codec<ActionFactory.Instance<T>> codec, Codec<ConditionFactory.Instance<V>> cond, Function<T, V> function) {
		Codec<List<ActionFactory.Instance<T>>> listCodec = OriginsCodecs.listOf(codec);
		Codec<FilterableWeightedList<ActionFactory.Instance<T>>> weightedListCodec = OriginsCodecs.weightedListOf(codec);
		registry.register(Origins.identifier("and"), () -> new ActionFactory<>(AndAction.codec(listCodec)));
		registry.register(Origins.identifier("chance"), () -> new ActionFactory<>(ChanceAction.codec(codec)));
		registry.register(Origins.identifier("if_else"), () -> new ActionFactory<>(IfElseAction.codec(codec, cond, function)));
		registry.register(Origins.identifier("choice"), () -> new ActionFactory<>(ChoiceAction.codec(weightedListCodec)));
		registry.register(Origins.identifier("if_else_list"), () -> new ActionFactory<>(IfElseListAction.codec(codec, cond, function)));
		registry.register(Origins.identifier("delay"), () -> new ActionFactory<>(DelayAction.codec(codec)));
	}
}
