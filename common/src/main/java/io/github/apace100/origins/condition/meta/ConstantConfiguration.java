package io.github.apace100.origins.condition.meta;

import com.mojang.serialization.Codec;

public record ConstantConfiguration<T>(boolean value) implements IDelegatedConditionConfiguration<T> {

	public static <T> Codec<ConstantConfiguration<T>> codec() {
		return Codec.BOOL.fieldOf("value").xmap(ConstantConfiguration<T>::new, ConstantConfiguration::value).codec();
	}

	@Override
	public boolean check(T parameters) {
		return this.value;
	}
}
