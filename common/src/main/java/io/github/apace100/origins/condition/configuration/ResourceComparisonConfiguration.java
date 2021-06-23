package io.github.apace100.origins.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.IntegerComparisonConfiguration;
import io.github.apace100.origins.api.configuration.PowerReference;

public record ResourceComparisonConfiguration(IntegerComparisonConfiguration comparison,
											  PowerReference resource) implements IOriginsFeatureConfiguration {
	public static Codec<ResourceComparisonConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			IntegerComparisonConfiguration.MAP_CODEC.forGetter(ResourceComparisonConfiguration::comparison),
			PowerReference.mapCodec("resource").forGetter(ResourceComparisonConfiguration::resource)
	).apply(instance, ResourceComparisonConfiguration::new));
}
