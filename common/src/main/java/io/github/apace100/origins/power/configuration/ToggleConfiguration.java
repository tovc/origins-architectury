package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.power.IActivePower;
import io.github.apace100.origins.api.power.configuration.power.ITogglePowerConfiguration;

public record ToggleConfiguration(boolean defaultState, IActivePower.Key key) implements ITogglePowerConfiguration {
	public static final Codec<ToggleConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("active_by_default", true).forGetter(ITogglePowerConfiguration::defaultState),
			IActivePower.Key.BACKWARD_COMPATIBLE_CODEC.optionalFieldOf("key", IActivePower.Key.PRIMARY).forGetter(ITogglePowerConfiguration::key)
	).apply(instance, ToggleConfiguration::new));
}
