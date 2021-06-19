package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.ActionOnCallbackConfiguration;
import net.minecraft.entity.player.PlayerEntity;

public class ActionOnCallbackPower extends PowerFactory<ActionOnCallbackConfiguration> {
	public ActionOnCallbackPower() {
		super(ActionOnCallbackConfiguration.CODEC);
	}

	@Override
	protected void onChosen(ActionOnCallbackConfiguration configuration, PlayerEntity player, boolean isOrbOfOrigin) {
		if (configuration.entityActionChosen() != null && (!isOrbOfOrigin || configuration.executeChosenWhenOrb()))
			configuration.entityActionChosen().execute(player);
	}

	@Override
	protected void onLost(ActionOnCallbackConfiguration configuration, PlayerEntity player) {
		if (configuration.entityActionLost() != null)
			configuration.entityActionLost().execute(player);
	}

	@Override
	protected void onAdded(ActionOnCallbackConfiguration configuration, PlayerEntity player) {
		if (configuration.entityActionAdded() != null)
			configuration.entityActionAdded().execute(player);
	}

	@Override
	protected void onRemoved(ActionOnCallbackConfiguration configuration, PlayerEntity player) {
		if (configuration.entityActionRemoved() != null)
			configuration.entityActionRemoved().execute(player);
	}

	@Override
	protected void onRespawn(ActionOnCallbackConfiguration configuration, PlayerEntity player) {
		if (configuration.entityActionRespawned() != null)
			configuration.entityActionRespawned().execute(player);
	}
}
