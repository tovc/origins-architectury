package io.github.apace100.origins.power.configuration.power;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record ParticleConfiguration(@Nullable ParticleEffect particle,
									int frequency) implements IOriginsFeatureConfiguration {

	public static final Codec<ParticleConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.PARTICLE_EFFECT.fieldOf("particle").forGetter(x -> Optional.ofNullable(x.particle())),
			Codec.intRange(1, Integer.MAX_VALUE).fieldOf("frequency").forGetter(ParticleConfiguration::frequency)
	).apply(instance, (particleEffect, integer) -> new ParticleConfiguration(particleEffect.orElse(null), integer)));

	@Override
	@NotNull
	public List<String> getWarnings(@NotNull MinecraftServer server) {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		if (particle() == null) builder.add("Optional particle was missing.");
		return ImmutableList.of();
	}

	@Override
	public boolean isConfigurationValid() {
		return particle() != null;
	}
}
