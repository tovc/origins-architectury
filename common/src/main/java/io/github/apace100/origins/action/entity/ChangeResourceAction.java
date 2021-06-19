package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.action.configuration.ChangeResourceConfiguration;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class ChangeResourceAction extends EntityAction<ChangeResourceConfiguration> {
	public ChangeResourceAction() {
		super(ChangeResourceConfiguration.CODEC);
	}

	@Override
	public void execute(ChangeResourceConfiguration configuration, Entity entity) {
		if (entity instanceof PlayerEntity player) {
			ConfiguredPower<?, ?> power = OriginsAPI.getComponent(player).getPower(configuration.resource());
			if (power != null) {
				power.change(player, configuration.amount());
				OriginComponent.sync(player);
			}
		}
	}
}
