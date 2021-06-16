package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

import java.util.Optional;

public record FieldConfiguration<T>(T value) implements IOriginsFeatureConfiguration {
	public static <T> Codec<FieldConfiguration<T>> codec(Codec<T> codec, String fieldName, T defaultValue) {
		return codec.optionalFieldOf(fieldName, defaultValue).xmap(FieldConfiguration::new, FieldConfiguration::value).codec();
	}

	public static <T> Codec<FieldConfiguration<T>> codec(Codec<T> codec, String fieldName) {
		return codec.fieldOf(fieldName).xmap(FieldConfiguration::new, FieldConfiguration::value).codec();
	}

	public static <T> Codec<FieldConfiguration<Optional<T>>> optionalCodec(Codec<T> codec, String fieldName) {
		return codec.optionalFieldOf(fieldName).xmap(FieldConfiguration::new, FieldConfiguration::value).codec();
	}
}
