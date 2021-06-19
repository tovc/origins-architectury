package io.github.apace100.origins.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

public record FoodConfiguration(int food, float saturation) implements IOriginsFeatureConfiguration {

	public static Codec<FoodConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("food").forGetter(FoodConfiguration::food),
			Codec.FLOAT.fieldOf("saturation").forGetter(FoodConfiguration::saturation)
	).apply(instance, FoodConfiguration::new));
}
