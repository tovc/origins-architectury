package io.github.apace100.origins.api.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;

import java.util.Optional;

public record IntegerComparisonConfiguration(Comparison comparison,
											 int compareTo) implements IOriginsFeatureConfiguration {
	public static final MapCodec<IntegerComparisonConfiguration> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(IntegerComparisonConfiguration::comparison),
			Codec.INT.fieldOf("compare_to").forGetter(IntegerComparisonConfiguration::compareTo)
	).apply(instance, IntegerComparisonConfiguration::new));

	public static final MapCodec<Optional<IntegerComparisonConfiguration>> OPTIONAL_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			OriginsCodecs.COMPARISON.optionalFieldOf("comparison").forGetter(x -> x.map(IntegerComparisonConfiguration::comparison)),
			Codec.INT.optionalFieldOf("compare_to").forGetter(x -> x.map(IntegerComparisonConfiguration::compareTo))
	).apply(instance, (t1, t2) -> t1.flatMap(x1 -> t2.map(x2 -> new IntegerComparisonConfiguration(x1, x2)))));

	public static final Codec<IntegerComparisonConfiguration> CODEC = MAP_CODEC.codec();

	public boolean check(int value) {
		return this.comparison().compare(value, this.compareTo());
	}

	public int getOptimalStoppingPoint() {
		return this.comparison().getOptimalStoppingIndex(this.compareTo());
	}
}
