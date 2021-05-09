package io.github.apace100.origins.power.condition;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.meta.ConstantCondition;
import io.github.apace100.origins.power.condition.meta.ListCondition;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.OriginsCodecs;
import me.shedaniel.architectury.registry.Registry;

import java.util.List;

public class MetaFactories {
	public static <T> void defineMetaConditions(Registry<ConditionFactory<T>> registry, Codec<ConditionFactory.Instance<T>> codec) {
		Codec<List<ConditionFactory.Instance<T>>> listCodec = OriginsCodecs.listOf(codec);
		registry.register(Origins.identifier("constant"), () -> new ConditionFactory<>(ConstantCondition.codec()));
		registry.register(Origins.identifier("and"), () -> new ConditionFactory<>(ListCondition.codec(listCodec, (t, ls) -> ls.stream().allMatch(x -> x.test(t)))));
		registry.register(Origins.identifier("or"), () -> new ConditionFactory<>(ListCondition.codec(listCodec, (t, ls) -> ls.stream().anyMatch(x -> x.test(t)))));
	}
}
