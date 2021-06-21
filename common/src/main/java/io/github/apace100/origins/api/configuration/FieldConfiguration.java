package io.github.apace100.origins.api.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record FieldConfiguration<T>(T value) implements IOriginsFeatureConfiguration {
	public static <T> FieldConfiguration<T> of(T value) {
		return new FieldConfiguration<>(value);
	}

	public static <T> Codec<FieldConfiguration<T>> codec(Codec<T> codec, String fieldName, T defaultValue) {
		return codec.optionalFieldOf(fieldName, defaultValue).xmap(FieldConfiguration::new, FieldConfiguration::value).codec();
	}

	public static <T> Codec<FieldConfiguration<T>> codec(MapCodec<T> codec) {
		return codec.xmap(FieldConfiguration::new, FieldConfiguration::value).codec();
	}

	public static <T> Codec<FieldConfiguration<T>> codec(Codec<T> codec, String fieldName) {
		return codec.fieldOf(fieldName).xmap(FieldConfiguration::new, FieldConfiguration::value).codec();
	}

	public static <T> Codec<FieldConfiguration<Optional<T>>> optionalCodec(Codec<T> codec, String fieldName) {
		return codec.optionalFieldOf(fieldName).xmap(FieldConfiguration::new, FieldConfiguration::value).codec();
	}

	@Override
	public @NotNull List<String> getErrors(@NotNull MinecraftServer server) {
		if (this.value() instanceof IOriginsFeatureConfiguration config)
			return config.copyErrorsFrom(config, server, this.name());
		return ImmutableList.of();
	}

	@Override
	public @NotNull List<String> getWarnings(@NotNull MinecraftServer server) {
		if (this.value() instanceof IOriginsFeatureConfiguration config)
			return config.copyWarningsFrom(config, server, this.name());
		return ImmutableList.of();
	}

	@Override
	public boolean isConfigurationValid() {
		return !(this.value() instanceof IOriginsFeatureConfiguration config) || config.isConfigurationValid();
	}
}
