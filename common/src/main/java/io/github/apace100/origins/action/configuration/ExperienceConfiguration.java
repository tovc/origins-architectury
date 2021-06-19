package io.github.apace100.origins.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

public record ExperienceConfiguration(int points, int levels) implements IOriginsFeatureConfiguration {

	public static final Codec<ExperienceConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("points", 0).forGetter(ExperienceConfiguration::points),
			Codec.INT.optionalFieldOf("levels", 0).forGetter(ExperienceConfiguration::levels)
	).apply(instance, ExperienceConfiguration::new));
}
