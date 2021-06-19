package io.github.apace100.origins.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.power.ListConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

public record SpawnEffectCloudConfiguration(float radius, float radiusOnUse, int waitTime,
											ListConfiguration<StatusEffectInstance> effects) implements IOriginsFeatureConfiguration {

	public static final Codec<SpawnEffectCloudConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.optionalFieldOf("radius", 3.0F).forGetter(x -> x.radius),
			Codec.FLOAT.optionalFieldOf("radius_on_use", -0.5F).forGetter(x -> x.radiusOnUse),
			Codec.INT.optionalFieldOf("wait_time", 10).forGetter(x -> x.waitTime),
			ListConfiguration.mapCodec(OriginsCodecs.STATUS_EFFECT_INSTANCE, "effect", "effects").forGetter(x -> x.effects)
	).apply(instance, SpawnEffectCloudConfiguration::new));
}
