package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.power.IActivePower;
import io.github.apace100.origins.api.power.configuration.power.ITogglePowerConfiguration;

public record ToggleNightVisionConfiguration(boolean defaultState, IActivePower.Key key, float strength) implements ITogglePowerConfiguration {
	public static final Codec<ToggleNightVisionConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("active_by_default", false).forGetter(ITogglePowerConfiguration::defaultState),
			IActivePower.Key.BACKWARD_COMPATIBLE_CODEC.optionalFieldOf("key", IActivePower.Key.PRIMARY).forGetter(ITogglePowerConfiguration::key),
			Codec.FLOAT.optionalFieldOf("strength", 1.0F).forGetter(ToggleNightVisionConfiguration::strength)
	).apply(instance, ToggleNightVisionConfiguration::new));
}
