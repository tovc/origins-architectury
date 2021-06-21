package io.github.apace100.origins.action.meta;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.configuration.IStreamConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * Represents configuration that is composed of sub action and will the given operation
 * on each of those.
 *
 * @param <T> The type of the actions that will be executed.
 * @param <V>
 */
public record StreamConfiguration<T, V>(List<T> entries, String name,
										BiConsumer<T, V> consumer) implements IDelegatedActionConfiguration<V>, IStreamConfiguration<T> {

	public static <T, V> StreamConfiguration<T, V> and(List<T> entries, BiConsumer<T, V> executor) {
		return new StreamConfiguration<>(entries, "And", executor);
	}

	public static <C, A, V> StreamConfiguration<Pair<C, A>, V> ifElseList(List<Pair<C, A>> entries, BiPredicate<C, V> predicate, BiConsumer<A, V> executor) {
		BiConsumer<Pair<C, A>, V> consumer = (pair, v) -> {
			if (predicate.test(pair.getFirst(), v)) executor.accept(pair.getSecond(), v);
		};
		return new StreamConfiguration<>(entries, "IfElseList", consumer);
	}

	public static <T, V> Codec<StreamConfiguration<T, V>> and(Codec<T> source, BiConsumer<T, V> executor) {
		return OriginsCodecs.listOf(source).fieldOf("actions").xmap(x -> and(x, executor), StreamConfiguration::entries).codec();
	}

	public static <C, A, V> Codec<StreamConfiguration<Pair<C, A>, V>> ifElseList(Codec<C> condition, Codec<A> action, BiPredicate<C, V> predicate, BiConsumer<A, V> executor) {
		Codec<Pair<C, A>> pairCodec = RecordCodecBuilder.create(instance -> instance.group(
				condition.fieldOf("condition").forGetter(Pair::getFirst),
				action.fieldOf("action").forGetter(Pair::getSecond)
		).apply(instance, Pair::of));
		return OriginsCodecs.listOf(pairCodec).fieldOf("actions").xmap(x -> ifElseList(x, predicate, executor), StreamConfiguration::entries).codec();
	}

	@Override
	public void execute(V parameters) {
		this.entries.forEach(t -> consumer.accept(t, parameters));
	}
}
