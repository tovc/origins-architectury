package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.action.configuration.PowerReference;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class PowerCondition extends EntityCondition<PowerReference> {

	public PowerCondition() {super(PowerReference.codec("power"));}

	@Override
	public boolean check(PowerReference configuration, LivingEntity entity) {
		OriginComponent component = OriginsAPI.getComponent(entity);
		return component.hasPower(configuration.power());
	}
}
