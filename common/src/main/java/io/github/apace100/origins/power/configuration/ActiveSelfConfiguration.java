package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.power.IActivePower;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.configuration.power.IActiveCooldownPowerConfiguration;
import io.github.apace100.origins.util.HudRender;

public record ActiveSelfConfiguration(int duration, HudRender hudRender,
									  ConfiguredEntityAction<?, ?> action,
									  IActivePower.Key key) implements IActiveCooldownPowerConfiguration {
	public static final Codec<ActiveSelfConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("cooldown").forGetter(ActiveSelfConfiguration::duration),
			HudRender.CODEC.fieldOf("hud_render").forGetter(ActiveSelfConfiguration::hudRender),
			ConfiguredEntityAction.CODEC.fieldOf("entity_action").forGetter(ActiveSelfConfiguration::action),
			IActivePower.Key.BACKWARD_COMPATIBLE_CODEC.optionalFieldOf("key", IActivePower.Key.PRIMARY).forGetter(ActiveSelfConfiguration::key)
	).apply(instance, ActiveSelfConfiguration::new));
}
