package io.github.apace100.origins.power.factory.meta.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.power.IFactory;

import java.util.Random;
import java.util.function.Consumer;

public class ChanceAction<T> implements Consumer<T> {

	public static <T> Codec<ChanceAction<T>> codec(Codec<IFactory.Instance<T>> codec) {
		return RecordCodecBuilder.create(instance -> instance.group(
				codec.fieldOf("action").forGetter(x -> x.action),
				Codec.FLOAT.fieldOf("chance").forGetter(x -> x.chance)
		).apply(instance, ChanceAction::new));
	}

	private final IFactory.Instance<T> action;
	private final float chance;

	public ChanceAction(IFactory.Instance<T> action, float chance) {
		this.action = action;
		this.chance = chance;
	}

	@Override
	public void accept(T t) {
		if (new Random().nextFloat() < this.chance)
			action.accept(t);
	}
}
