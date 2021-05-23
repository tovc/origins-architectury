package io.github.apace100.origins.power.factory.meta.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.factory.action.ActionFactory;

import java.util.List;
import java.util.function.Consumer;

public class AndAction<T> implements Consumer<T> {

	public static <T> Codec<AndAction<T>> codec(Codec<List<ActionFactory.Instance<T>>> listCodec) {
		return RecordCodecBuilder.create(instance -> instance.group(
				listCodec.fieldOf("actions").forGetter(x -> x.actions)
		).apply(instance, AndAction::new));
	}

	private final List<ActionFactory.Instance<T>> actions;

	public AndAction(List<ActionFactory.Instance<T>> actions) {this.actions = actions;}

	@Override
	public void accept(T t) {
		this.actions.forEach(x -> x.accept(t));
	}
}
