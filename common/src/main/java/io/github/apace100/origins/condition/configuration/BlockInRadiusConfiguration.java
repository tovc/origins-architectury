package io.github.apace100.origins.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.IntegerComparisonConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import io.github.apace100.origins.util.OriginsCodecs;
import io.github.apace100.origins.util.Shape;

public record BlockInRadiusConfiguration(
		ConfiguredBlockCondition<?, ?> blockCondition, int radius,
		Shape shape, IntegerComparisonConfiguration comparison) implements IOriginsFeatureConfiguration {

	public static final Codec<BlockInRadiusConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredBlockCondition.CODEC.fieldOf("block_condition").forGetter(x -> x.blockCondition),
			Codec.INT.fieldOf("radius").forGetter(x -> x.radius),
			OriginsCodecs.SHAPE.optionalFieldOf("shape", Shape.CUBE).forGetter(x -> x.shape),
			IntegerComparisonConfiguration.MAP_CODEC.forGetter(x -> x.comparison)
	).apply(instance, BlockInRadiusConfiguration::new));
}
