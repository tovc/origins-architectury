package io.github.apace100.origins.power.factory;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.IFactory;
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

	public static <T, V> void defineMetaActions(Registry<IFactory<T>> registry, Codec<IFactory.Instance<T>> codec, Codec<ConditionFactory.Instance<V>> cond, Function<T, V> function) {
		Codec<List<IFactory.Instance<T>>> listCodec = OriginsCodecs.listOf(codec);
		Codec<FilterableWeightedList<IFactory.Instance<T>>> weightedListCodec = OriginsCodecs.weightedListOf(codec);
		registry.register(Origins.identifier("and"), () -> new IFactory<>(AndAction.codec(listCodec)));
		registry.register(Origins.identifier("chance"), () -> new IFactory<>(ChanceAction.codec(codec)));
		registry.register(Origins.identifier("if_else"), () -> new IFactory<>(IfElseAction.codec(codec, cond, function)));
		registry.register(Origins.identifier("choice"), () -> new IFactory<>(ChoiceAction.codec(weightedListCodec)));
		registry.register(Origins.identifier("if_else_list"), () -> new IFactory<>(IfElseListAction.codec(codec, cond, function)));
		registry.register(Origins.identifier("delay"), () -> new IFactory<>(DelayAction.codec(codec)));
	}
}
