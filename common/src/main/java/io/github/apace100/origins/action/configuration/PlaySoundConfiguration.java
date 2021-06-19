package io.github.apace100.origins.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.sound.SoundEvent;

import java.util.Optional;

public record PlaySoundConfiguration(Optional<SoundEvent> sound, float volume,
									 float pitch) implements IOriginsFeatureConfiguration {

	public static final Codec<PlaySoundConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_SOUND_EVENT.fieldOf("sound").forGetter(PlaySoundConfiguration::sound),
			Codec.FLOAT.optionalFieldOf("volume", 1F).forGetter(PlaySoundConfiguration::volume),
			Codec.FLOAT.optionalFieldOf("pitch", 1F).forGetter(PlaySoundConfiguration::pitch)
	).apply(instance, PlaySoundConfiguration::new));
}
