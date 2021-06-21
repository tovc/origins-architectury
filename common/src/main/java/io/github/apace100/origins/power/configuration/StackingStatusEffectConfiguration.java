package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.ListConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.effect.StatusEffectInstance;

public record StackingStatusEffectConfiguration(ListConfiguration<StatusEffectInstance> effects,
												int min, int max, int duration) implements IOriginsFeatureConfiguration {
	public static final Codec<StackingStatusEffectConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.mapCodec(OriginsCodecs.STATUS_EFFECT_INSTANCE, "effect", "effects").forGetter(StackingStatusEffectConfiguration::effects),
			Codec.INT.fieldOf("min_stacks").forGetter(StackingStatusEffectConfiguration::min),
			Codec.INT.fieldOf("max_stacks").forGetter(StackingStatusEffectConfiguration::min),
			Codec.INT.fieldOf("duration_per_stack").forGetter(StackingStatusEffectConfiguration::duration)
	).apply(instance, StackingStatusEffectConfiguration::new));
}
