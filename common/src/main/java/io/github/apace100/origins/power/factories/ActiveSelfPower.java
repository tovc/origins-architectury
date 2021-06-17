package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.power.ActiveCooldownPowerFactory;
import io.github.apace100.origins.power.configuration.power.ActiveSelfConfiguration;
import net.minecraft.entity.player.PlayerEntity;

public class ActiveSelfPower extends ActiveCooldownPowerFactory.Simple<ActiveSelfConfiguration> {
	public ActiveSelfPower() {
		super(ActiveSelfConfiguration.CODEC);
	}

	@Override
	protected void execute(ConfiguredPower<ActiveSelfConfiguration, ?> configuration, PlayerEntity player) {
		configuration.getConfiguration().action().execute(player);
	}
}
