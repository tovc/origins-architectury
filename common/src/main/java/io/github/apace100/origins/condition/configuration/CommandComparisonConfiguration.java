package io.github.apace100.origins.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.action.configuration.CommandConfiguration;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.IntegerComparisonConfiguration;

public record CommandComparisonConfiguration(CommandConfiguration command,
											 IntegerComparisonConfiguration comparison) implements IOriginsFeatureConfiguration {
	public static final Codec<CommandComparisonConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			CommandConfiguration.MAP_CODEC.forGetter(CommandComparisonConfiguration::command),
			IntegerComparisonConfiguration.MAP_CODEC.forGetter(CommandComparisonConfiguration::comparison)
	).apply(instance, CommandComparisonConfiguration::new));
}
