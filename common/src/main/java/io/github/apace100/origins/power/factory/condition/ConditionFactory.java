package io.github.apace100.origins.power.factory.condition;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.power.factory.GenericFactory;
import io.github.apace100.origins.power.factory.GenericInstance;

import java.util.function.Predicate;

public final class ConditionFactory<T> extends GenericFactory<ConditionFactory.Instance<T>, ConditionFactory<T>> {
	public ConditionFactory(Codec<? extends Predicate<T>> codec) {
		super(codec, Instance::new, Instance::getPredicate);
	}

	public static final class Instance<T> extends GenericInstance<Instance<T>, ConditionFactory<T>> implements Predicate<T> {

		private final Predicate<T> predicate;

		public Instance(ConditionFactory<T> factory, Predicate<T> predicate) {
			super(factory);
			this.predicate = predicate;
		}

		public Predicate<T> getPredicate() {
			return predicate;
		}

		@Override
		public boolean test(T t) {
			return this.predicate.test(t);
		}
	}
}
