package io.github.apace100.origins.power.configuration.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

public record ExhaustOverTimeConfiguration(int interval, float exhaustion) implements IOriginsFeatureConfiguration {
	public static final Codec<ExhaustOverTimeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("interval").forGetter(ExhaustOverTimeConfiguration::interval),
			Codec.FLOAT.fieldOf("exhaustion").forGetter(ExhaustOverTimeConfiguration::exhaustion)
	).apply(instance, ExhaustOverTimeConfiguration::new));
}
