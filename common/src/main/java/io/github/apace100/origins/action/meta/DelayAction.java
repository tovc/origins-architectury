package io.github.apace100.origins.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.Scheduler;

import java.util.function.BiConsumer;

public record DelayAction<T, V>(T action, int delay, BiConsumer<T, V> executor) implements IDelegatedActionConfiguration<V> {
	private static final Scheduler SCHEDULER = new Scheduler();

	public static <T, V> Codec<DelayAction<T, V>> codec(Codec<T> codec, BiConsumer<T, V> executor) {
		return RecordCodecBuilder.create(instance -> instance.group(
				codec.fieldOf("action").forGetter(DelayAction::action),
				Codec.INT.fieldOf("ticks").forGetter(DelayAction::delay)
		).apply(instance, (action, delay) -> new DelayAction<>(action, delay, executor)));
	}

	@Override
	public void execute(V parameters) {
		SCHEDULER.queue(m -> this.executor().accept(this.action(), parameters), this.delay());
	}
}
