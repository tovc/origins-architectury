package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

public record NoConfiguration() implements IOriginsFeatureConfiguration {
	public static final Codec<NoConfiguration> CODEC = Codec.unit(NoConfiguration::new);
}
