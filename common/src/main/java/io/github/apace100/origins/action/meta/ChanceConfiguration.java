package io.github.apace100.origins.action.meta;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Random;
import java.util.function.BiConsumer;

public record ChanceConfiguration<T, V>(float chance, T action, BiConsumer<T, V> executor) implements IDelegatedActionConfiguration<V>{
	public static <T, V>Codec<ChanceConfiguration<T, V>> codec(Codec<T> codec, BiConsumer<T, V> executor) {
		return RecordCodecBuilder.create(instance -> instance.group(
				Codec.FLOAT.fieldOf("chance").forGetter(ChanceConfiguration::chance),
				codec.fieldOf("action").forGetter(ChanceConfiguration::action)
		).apply(instance, (chance, action) -> new ChanceConfiguration<>(chance, action, executor)));
	}

	@Override
	public void execute(V parameters) {
		if (new Random().nextFloat() < this.chance())
			this.executor().accept(this.action(), parameters);
	}
}
