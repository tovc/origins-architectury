package io.github.apace100.origins.factory;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.action.meta.*;
import io.github.apace100.origins.factory.condition.ConditionFactory;
import io.github.apace100.origins.factory.meta.condition.ConstantCondition;
import io.github.apace100.origins.factory.meta.condition.ListCondition;
import io.github.apace100.origins.util.OriginsCodecs;
import me.shedaniel.architectury.registry.Registry;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class MetaFactories {

	public static <T> void defineMetaConditions(Registry<ConditionFactory<T>> registry, Codec<ConditionFactory.Instance<T>> codec) {
		Codec<List<ConditionFactory.Instance<T>>> listCodec = OriginsCodecs.listOf(codec);
		registry.register(Origins.identifier("constant"), () -> new ConditionFactory<>(ConstantCondition.codec()));
		registry.register(Origins.identifier("and"), () -> new ConditionFactory<>(ListCondition.codec(listCodec, (t, ls) -> ls.stream().allMatch(x -> x.test(t)))));
		registry.register(Origins.identifier("or"), () -> new ConditionFactory<>(ListCondition.codec(listCodec, (t, ls) -> ls.stream().anyMatch(x -> x.test(t)))));
	}

	public static <F, A, C, V> void defineMetaActions(Registry<F> registry, Function<Codec<? extends IDelegatedActionConfiguration<V>>, ? extends F> func, Codec<A> actionCodec, Codec<C> conditionCodec, BiConsumer<A, V> executor, BiPredicate<C, V> predicate) {
		registry.registerSupplied(Origins.identifier("and"), () -> func.apply(StreamConfiguration.and(actionCodec, executor)));
		registry.registerSupplied(Origins.identifier("chance"), () -> func.apply(ChanceConfiguration.codec(actionCodec, executor)));
		registry.registerSupplied(Origins.identifier("if_else"), () -> func.apply(IfElseConfiguration.codec(conditionCodec, actionCodec, predicate, executor)));
		registry.registerSupplied(Origins.identifier("if_else_list"), () -> func.apply(StreamConfiguration.ifElseList(conditionCodec, actionCodec, predicate, executor)));
		registry.registerSupplied(Origins.identifier("choice"), () -> func.apply(ChoiceConfiguration.codec(actionCodec, executor)));
		registry.registerSupplied(Origins.identifier("delay"), () -> func.apply(DelayAction.codec(actionCodec, executor)));
	}
}
