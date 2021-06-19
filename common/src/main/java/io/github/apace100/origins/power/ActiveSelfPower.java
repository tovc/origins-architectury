package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.power.ActiveCooldownPowerFactory;
import io.github.apace100.origins.power.configuration.ActiveSelfConfiguration;
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
