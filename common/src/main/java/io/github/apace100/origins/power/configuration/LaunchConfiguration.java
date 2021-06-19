package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.power.IActivePower;
import io.github.apace100.origins.api.power.configuration.power.IActiveCooldownPowerConfiguration;
import io.github.apace100.origins.api.power.configuration.power.ICooldownPowerConfiguration;
import io.github.apace100.origins.util.HudRender;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record LaunchConfiguration(int duration, float speed, @Nullable SoundEvent sound, HudRender hudRender,
								  IActivePower.Key key) implements IActiveCooldownPowerConfiguration {
	public static final Codec<LaunchConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("cooldown").forGetter(ICooldownPowerConfiguration::duration),
			Codec.FLOAT.fieldOf("speed").forGetter(LaunchConfiguration::speed),
			OriginsCodecs.OPTIONAL_SOUND_EVENT.optionalFieldOf("sound", Optional.empty()).forGetter(x -> Optional.ofNullable(x.sound())),
			HudRender.CODEC.fieldOf("hud_render").forGetter(ICooldownPowerConfiguration::hudRender),
			IActivePower.Key.CODEC.optionalFieldOf("key", IActivePower.Key.PRIMARY).forGetter(IActiveCooldownPowerConfiguration::key)
	).apply(instance, (t1, t2, t3, t4, t5) -> new LaunchConfiguration(t1, t2, t3.orElse(null), t4, t5)));
}
