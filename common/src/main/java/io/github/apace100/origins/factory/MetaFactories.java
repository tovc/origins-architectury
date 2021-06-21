package io.github.apace100.origins.factory;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.action.meta.*;
import io.github.apace100.origins.condition.meta.IDelegatedConditionConfiguration;
import io.github.apace100.origins.factory.condition.ConditionFactory;
import io.github.apace100.origins.condition.meta.ConstantConfiguration;
import io.github.apace100.origins.condition.meta.ConditionStreamConfiguration;
import me.shedaniel.architectury.registry.Registry;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class MetaFactories {

	public static <F, C, V> void defineMetaConditions(Registry<F> registry, Function<Codec<? extends IDelegatedConditionConfiguration<V>>, ? extends F> func, Codec<C> conditionCodec, BiPredicate<C, V> predicate) {
		registry.register(Origins.identifier("constant"), () -> func.apply(ConstantConfiguration.codec()));
		registry.register(Origins.identifier("and"), () -> func.apply(ConditionStreamConfiguration.andCodec(conditionCodec, predicate)));
		registry.register(Origins.identifier("or"), () -> func.apply(ConditionStreamConfiguration.orCodec(conditionCodec, predicate)));
	}

	public static <F, A, C, V> void defineMetaActions(Registry<F> registry, Function<Codec<? extends IDelegatedActionConfiguration<V>>, ? extends F> func, Codec<A> actionCodec, Codec<C> conditionCodec, BiConsumer<A, V> executor, BiPredicate<C, V> predicate) {
		registry.registerSupplied(Origins.identifier("and"), () -> func.apply(io.github.apace100.origins.action.meta.StreamConfiguration.and(actionCodec, executor)));
		registry.registerSupplied(Origins.identifier("chance"), () -> func.apply(ChanceConfiguration.codec(actionCodec, executor)));
		registry.registerSupplied(Origins.identifier("if_else"), () -> func.apply(IfElseConfiguration.codec(conditionCodec, actionCodec, predicate, executor)));
		registry.registerSupplied(Origins.identifier("if_else_list"), () -> func.apply(io.github.apace100.origins.action.meta.StreamConfiguration.ifElseList(conditionCodec, actionCodec, predicate, executor)));
		registry.registerSupplied(Origins.identifier("choice"), () -> func.apply(ChoiceConfiguration.codec(actionCodec, executor)));
		registry.registerSupplied(Origins.identifier("delay"), () -> func.apply(DelayAction.codec(actionCodec, executor)));
	}
}
