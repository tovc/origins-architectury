package io.github.apace100.origins.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.configuration.IStreamConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

public record IfElseConfiguration<C, A, V>(C condition, A ifAction, @Nullable A elseAction, BiPredicate<C, V> predicate,
										   BiConsumer<A, V> executor) implements IDelegatedActionConfiguration<V>, IStreamConfiguration<Object> {
	public static <C, A, V> Codec<IfElseConfiguration<C, A, V>> codec(Codec<C> conditionCodec, Codec<A> actionCodec, BiPredicate<C, V> predicate, BiConsumer<A, V> executor) {
		return RecordCodecBuilder.create(instance -> instance.group(
				conditionCodec.fieldOf("condition").forGetter(IfElseConfiguration::condition),
				actionCodec.fieldOf("if_action").forGetter(IfElseConfiguration::ifAction),
				actionCodec.optionalFieldOf("else_action").forGetter(x -> Optional.ofNullable(x.elseAction()))
		).apply(instance, (c, i, e) -> new IfElseConfiguration<>(c, i, e.orElse(null), predicate, executor)));
	}

	@Override
	public void execute(V parameters) {
		if (this.predicate().test(this.condition(), parameters))
			this.executor().accept(this.ifAction(), parameters);
		else if (this.elseAction() != null)
			this.executor().accept(this.elseAction(), parameters);
	}

	@Override
	public List<Object> entries() {
		return Stream.of(this.condition(), this.ifAction(), this.elseAction()).toList();
	}
}
