package io.github.apace100.origins.factory.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.factory.GenericFactory;
import io.github.apace100.origins.factory.GenericInstance;

import java.util.function.Function;
import java.util.function.Predicate;

public final class ConditionFactory<T> extends GenericFactory<ConditionFactory.Instance<T>, ConditionFactory<T>> {

	private static <T, V extends Predicate<T>> Function<ConditionFactory<T>, Codec<Instance<T>>> addFields(Codec<V> codec) {
		MapCodec<V> codec1;
		if (codec instanceof MapCodec.MapCodecCodec)
			codec1 = ((MapCodec.MapCodecCodec<V>) codec).codec();
		else
			codec1 = codec.fieldOf("value");
		return f -> RecordCodecBuilder.create(instance -> instance.group(
				codec1.forGetter(Instance::getPredicate),
				Codec.BOOL.optionalFieldOf("inverted", false).forGetter(x -> x.inverted)
		).apply(instance, (predicate, inv) -> new Instance<T>(f, predicate, inv)));
	}

	public ConditionFactory(Codec<? extends Predicate<T>> codec) {
		super(addFields(codec));
	}

	public static final class Instance<T> extends GenericInstance<Instance<T>, ConditionFactory<T>> implements Predicate<T> {

		private final Predicate<T> predicate;
		private final boolean inverted;

		public Instance(ConditionFactory<T> factory, Predicate<T> predicate, boolean inverted) {
			super(factory);
			this.predicate = predicate;
			this.inverted = inverted;
		}

		public <K extends Predicate<T>> K getPredicate() {
			return (K) predicate;
		}

		@Override
		public boolean test(T t) {
			return this.inverted ^ this.predicate.test(t);
		}
	}
}
