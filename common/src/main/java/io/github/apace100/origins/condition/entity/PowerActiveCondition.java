package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.configuration.PowerReference;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class PowerActiveCondition extends EntityCondition<PowerReference> {

	public PowerActiveCondition() {super(PowerReference.codec("power"));}

	@Override
	public boolean check(PowerReference configuration, LivingEntity entity) {
		OriginComponent component = OriginsAPI.getComponent(entity);
		return entity instanceof PlayerEntity player && component.hasPower(configuration.power()) && component.getPower(configuration.power()).isActive(player);
	}
}
