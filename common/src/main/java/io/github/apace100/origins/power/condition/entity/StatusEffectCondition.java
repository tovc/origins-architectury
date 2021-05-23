package io.github.apace100.origins.power.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.Optional;
import java.util.function.Predicate;

public class StatusEffectCondition implements Predicate<LivingEntity> {

	public static final Codec<StatusEffectCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_STATUS_EFFECT.fieldOf("effect").forGetter(x -> x.effect),
			Codec.INT.optionalFieldOf("min_amplifier", 0).forGetter(x -> x.minAmplifier),
			Codec.INT.optionalFieldOf("max_amplifier", Integer.MAX_VALUE).forGetter(x -> x.maxAmplifier),
			Codec.INT.optionalFieldOf("min_duration", 0).forGetter(x -> x.minDuration),
			Codec.INT.optionalFieldOf("max_duration", Integer.MAX_VALUE).forGetter(x -> x.maxDuration)
	).apply(instance, StatusEffectCondition::new));

	private final Optional<StatusEffect> effect;
	private final int minAmplifier;
	private final int maxAmplifier;
	private final int minDuration;
	private final int maxDuration;

	public StatusEffectCondition(Optional<StatusEffect> effect, int minAmplifier, int maxAmplifier, int minDuration, int maxDuration) {
		this.effect = effect;
		this.minAmplifier = minAmplifier;
		this.maxAmplifier = maxAmplifier;
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
	}

	@Override
	public boolean test(LivingEntity entity) {
		if (!effect.isPresent() || !entity.hasStatusEffect(effect.get()))
			return false;
		StatusEffectInstance instance = entity.getStatusEffect(effect.get());
		return instance.getDuration() <= maxDuration && instance.getDuration() >= minDuration
			   && instance.getAmplifier() <= maxAmplifier && instance.getAmplifier() >= minAmplifier;
	}
}
