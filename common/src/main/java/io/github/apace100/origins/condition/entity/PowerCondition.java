package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.configuration.PowerReference;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import net.minecraft.entity.LivingEntity;

public class PowerCondition extends EntityCondition<PowerReference> {

	public PowerCondition() {super(PowerReference.codec("power"));}

	@Override
	public boolean check(PowerReference configuration, LivingEntity entity) {
		return OriginsAPI.getComponent(entity).hasPower(configuration.power());
	}
}
