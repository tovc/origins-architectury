package io.github.apace100.origins.power.action.entity;

import io.github.apace100.origins.power.configuration.ListConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

public class ApplyEffectAction extends EntityAction<ListConfiguration<StatusEffectInstance>> {

	public ApplyEffectAction() {
		super(ListConfiguration.codec(OriginsCodecs.STATUS_EFFECT_INSTANCE, "effect", "effects"));
	}

	@Override
	public void execute(ListConfiguration<StatusEffectInstance> configuration, Entity entity) {
		if (!(entity instanceof LivingEntity))
			return;
		for (StatusEffectInstance effect : configuration.getContent())
			((LivingEntity) entity).applyStatusEffect(new StatusEffectInstance(effect));
	}
}
