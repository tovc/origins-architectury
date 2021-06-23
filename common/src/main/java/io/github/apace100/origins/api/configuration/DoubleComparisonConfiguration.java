package io.github.apace100.origins.api.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;

public record DoubleComparisonConfiguration(Comparison comparison,
											double compareTo) implements IOriginsFeatureConfiguration {
	public static final MapCodec<DoubleComparisonConfiguration> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(DoubleComparisonConfiguration::comparison),
			Codec.DOUBLE.fieldOf("compare_to").forGetter(DoubleComparisonConfiguration::compareTo)
	).apply(instance, DoubleComparisonConfiguration::new));

	public static final Codec<DoubleComparisonConfiguration> CODEC = MAP_CODEC.codec();

	public boolean check(double value) {
		return this.comparison().compare(value, this.compareTo());
	}
}
