package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;

import java.util.Optional;

public class ClearEffectAction extends EntityAction<FieldConfiguration<Optional<StatusEffect>>> {

	public ClearEffectAction() {
		super(FieldConfiguration.codec(OriginsCodecs.OPTIONAL_STATUS_EFFECT, "effect", Optional.empty()));
	}

	@Override
	public void execute(FieldConfiguration<Optional<StatusEffect>> configuration, Entity entity) {
		if (entity instanceof LivingEntity living)
			configuration.value().ifPresentOrElse(living::removeStatusEffect, living::clearStatusEffects);
	}
}
