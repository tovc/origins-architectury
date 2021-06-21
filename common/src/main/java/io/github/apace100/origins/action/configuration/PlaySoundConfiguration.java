package io.github.apace100.origins.action.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record PlaySoundConfiguration(@Nullable SoundEvent sound, float volume,
									 float pitch) implements IOriginsFeatureConfiguration {

	public static final Codec<PlaySoundConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_SOUND_EVENT.fieldOf("sound").forGetter(x -> Optional.ofNullable(x.sound())),
			Codec.FLOAT.optionalFieldOf("volume", 1F).forGetter(PlaySoundConfiguration::volume),
			Codec.FLOAT.optionalFieldOf("pitch", 1F).forGetter(PlaySoundConfiguration::pitch)
	).apply(instance, (t1, t2, t3) -> new PlaySoundConfiguration(t1.orElse(null), t2, t3)));

	@Override
	public @NotNull List<String> getWarnings(@NotNull MinecraftServer server) {
		if (this.sound() == null)
			return ImmutableList.of("PlaySound/Missing sound");
		return IOriginsFeatureConfiguration.super.getWarnings(server);
	}

	@Override
	public boolean isConfigurationValid() {
		return this.sound() != null;
	}
}
