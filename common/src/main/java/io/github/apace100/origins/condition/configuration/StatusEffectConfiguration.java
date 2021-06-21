package io.github.apace100.origins.condition.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record StatusEffectConfiguration(@Nullable StatusEffect effect,
										int minAmplifier, int maxAmplifier, int minDuration,
										int maxDuration) implements IOriginsFeatureConfiguration {

	public static final Codec<StatusEffectConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_STATUS_EFFECT.fieldOf("effect").forGetter(x -> Optional.ofNullable(x.effect())),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("min_amplifier", 0).forGetter(StatusEffectConfiguration::minAmplifier),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_amplifier", Integer.MAX_VALUE).forGetter(StatusEffectConfiguration::maxAmplifier),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("min_duration", 0).forGetter(StatusEffectConfiguration::minDuration),
			Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_duration", Integer.MAX_VALUE).forGetter(StatusEffectConfiguration::maxDuration)
	).apply(instance, (t1, t2, t3, t4, t5) -> new StatusEffectConfiguration(t1.orElse(null), t2, t3, t4, t5)));

	@Override
	public boolean isConfigurationValid() {
		return effect() != null && this.minAmplifier() <= this.maxAmplifier() && this.minDuration() <= this.maxDuration();
	}

	@Override
	public @NotNull List<String> getWarnings(@NotNull MinecraftServer server) {
		if (effect() == null)
			return ImmutableList.of(this.name() + "/Missing Effect");
		return ImmutableList.of();
	}

	@Override
	public @NotNull List<String> getErrors(@NotNull MinecraftServer server) {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		if (this.minAmplifier() <= this.maxAmplifier()) builder.add("%s/Amplifier range is invalid: [%d,%d]".formatted(name(), this.minAmplifier(), this.maxAmplifier()));
		if (this.minDuration() <= this.maxDuration()) builder.add("%s/Duration range is invalid: [%d,%d]".formatted(name(), this.minDuration(), this.maxDuration()));
		return builder.build();
	}
}
