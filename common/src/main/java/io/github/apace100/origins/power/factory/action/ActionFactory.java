package io.github.apace100.origins.power.factory.action;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.power.factory.GenericFactory;
import io.github.apace100.origins.power.factory.GenericInstance;

import java.util.function.Consumer;

public class ActionFactory<T> extends GenericFactory<ActionFactory.Instance<T>, ActionFactory<T>> {
	public ActionFactory(Codec<? extends Consumer<T>> codec) {
		super(codec, Instance::new, Instance::getConsumer);
	}

	public static class Instance<T> extends GenericInstance<Instance<T>, ActionFactory<T>> implements Consumer<T> {

		private final Consumer<T> consumer;

		public Instance(ActionFactory<T> factory, Consumer<T> consumer) {
			super(factory);
			this.consumer = consumer;
		}

		public Consumer<T> getConsumer() {
			return consumer;
		}

		@Override
		public void accept(T t) {

		}
	}
}
