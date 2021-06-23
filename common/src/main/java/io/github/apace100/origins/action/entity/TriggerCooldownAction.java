package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.api.configuration.PowerReference;
import io.github.apace100.origins.api.power.ICooldownPower;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.EntityAction;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class TriggerCooldownAction extends EntityAction<PowerReference> {
	public TriggerCooldownAction() {
		super(PowerReference.codec("power"));
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void execute(PowerReference configuration, Entity entity) {
		if (entity instanceof PlayerEntity player) {
			ConfiguredPower<?, ?> power = ModComponentsArchitectury.getOriginComponent(entity).getPower(configuration.power());
			if (power.getFactory() instanceof ICooldownPower cp) {
				cp.use(power, player);
			}
		}
	}
}
