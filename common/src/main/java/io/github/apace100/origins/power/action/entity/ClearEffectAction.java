package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;

import java.util.Optional;
import java.util.function.Consumer;

public class ClearEffectAction implements Consumer<Entity> {

	public static final Codec<ClearEffectAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_STATUS_EFFECT.optionalFieldOf("effect", Optional.empty()).forGetter(x -> x.effect)
	).apply(instance, ClearEffectAction::new));

	private final Optional<StatusEffect> effect;

	public ClearEffectAction(Optional<StatusEffect> effect) {this.effect = effect;}

	@Override
	public void accept(Entity entity) {
		if (entity instanceof LivingEntity) {
			if (effect.isPresent())
				((LivingEntity) entity).removeStatusEffect(effect.get());
			else
				((LivingEntity) entity).clearStatusEffects();
		}
	}
}
