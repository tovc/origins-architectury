package io.github.apace100.origins.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.action.entity.DamageAction;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.damage.DamageSource;

public record DamageConfiguration(DamageSource source, float amount) implements IOriginsFeatureConfiguration {
	public static final Codec<DamageConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.DAMAGE_SOURCE.fieldOf("source").forGetter(DamageConfiguration::source),
			Codec.FLOAT.fieldOf("amount").forGetter(DamageConfiguration::amount)
	).apply(instance, DamageConfiguration::new));
}
