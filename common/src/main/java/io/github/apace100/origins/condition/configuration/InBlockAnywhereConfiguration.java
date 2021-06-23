package io.github.apace100.origins.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.IntegerComparisonConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;

public record InBlockAnywhereConfiguration(
		ConfiguredBlockCondition<?, ?> blockCondition,
		IntegerComparisonConfiguration comparison) implements IOriginsFeatureConfiguration {

	public static final Codec<InBlockAnywhereConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredBlockCondition.CODEC.fieldOf("block_condition").forGetter(x -> x.blockCondition),
			IntegerComparisonConfiguration.MAP_CODEC.forGetter(x -> x.comparison)
	).apply(instance, InBlockAnywhereConfiguration::new));
}
