package io.github.edwinmindcraft.origins.common.power.configuration;

import com.mojang.serialization.Codec;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;

public record WaterVisionConfiguration(float strength) implements IDynamicFeatureConfiguration {
	public static Codec<WaterVisionConfiguration> CODEC = Codec.FLOAT.optionalFieldOf("strength", 1.0F).xmap(WaterVisionConfiguration::new, WaterVisionConfiguration::strength).codec();
}
