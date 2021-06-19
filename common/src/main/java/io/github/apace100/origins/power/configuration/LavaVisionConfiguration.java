package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

public record LavaVisionConfiguration(float s, float v) implements IOriginsFeatureConfiguration {
	public static final Codec<LavaVisionConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("s").forGetter(LavaVisionConfiguration::s),
			Codec.FLOAT.fieldOf("v").forGetter(LavaVisionConfiguration::v)
	).apply(instance, LavaVisionConfiguration::new));
}
