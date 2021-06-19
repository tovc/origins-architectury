package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.power.configuration.power.ICooldownPowerConfiguration;
import io.github.apace100.origins.util.HudRender;

public record CooldownConfiguration(int duration, HudRender hudRender) implements ICooldownPowerConfiguration {
	public static final Codec<CooldownConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("cooldown").forGetter(CooldownConfiguration::duration),
			HudRender.CODEC.fieldOf("hud_render").forGetter(CooldownConfiguration::hudRender)
	).apply(instance, CooldownConfiguration::new));
}
