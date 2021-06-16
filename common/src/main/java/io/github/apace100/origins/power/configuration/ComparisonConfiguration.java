package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;

public record ComparisonConfiguration(Comparison comparison, float compareTo) {
	public static final Codec<ComparisonConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(ComparisonConfiguration::comparison),
			Codec.FLOAT.fieldOf("compare_to").forGetter(ComparisonConfiguration::compareTo)
	).apply(instance, ComparisonConfiguration::new));
}
