package io.github.apace100.origins.power.factory.meta.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.Scheduler;

import java.util.function.Consumer;

public class DelayAction<T> implements Consumer<T> {
	private static final Scheduler SCHEDULER = new Scheduler();

	public static <T> Codec<DelayAction<T>> codec(Codec<ActionFactory.Instance<T>> codec) {
		return RecordCodecBuilder.create(instance -> instance.group(
				codec.fieldOf("action").forGetter(x -> x.action),
				Codec.INT.fieldOf("ticks").forGetter(x -> x.delay)
		).apply(instance, DelayAction::new));
	}

	private final ActionFactory.Instance<T> action;
	private final int delay;

	public DelayAction(ActionFactory.Instance<T> action, int delay) {
		this.action = action;
		this.delay = delay;
	}

	@Override
	public void accept(T t) {
		SCHEDULER.queue(m -> this.action.accept(t), this.delay);
	}
}
