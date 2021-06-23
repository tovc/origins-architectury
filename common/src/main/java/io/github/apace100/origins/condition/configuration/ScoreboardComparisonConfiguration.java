package io.github.apace100.origins.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.DoubleComparisonConfiguration;

public record ScoreboardComparisonConfiguration(DoubleComparisonConfiguration comparison,
												String objective) implements IOriginsFeatureConfiguration {
	public static Codec<ScoreboardComparisonConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DoubleComparisonConfiguration.MAP_CODEC.forGetter(ScoreboardComparisonConfiguration::comparison),
			Codec.STRING.fieldOf("objective").forGetter(ScoreboardComparisonConfiguration::objective)
	).apply(instance, ScoreboardComparisonConfiguration::new));
}
