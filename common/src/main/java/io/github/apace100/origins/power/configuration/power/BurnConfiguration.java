package io.github.apace100.origins.power.configuration.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

public record BurnConfiguration(int interval, int duration) implements IOriginsFeatureConfiguration {
	public static final Codec<BurnConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("interval").forGetter(BurnConfiguration::interval),
			Codec.INT.fieldOf("burn_duration").forGetter(BurnConfiguration::duration)
	).apply(instance, BurnConfiguration::new));
}
