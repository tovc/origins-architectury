package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.power.ActiveCooldownPowerFactory;
import io.github.apace100.origins.power.configuration.FireProjectileConfiguration;
import net.minecraft.entity.player.PlayerEntity;

public class FireProjectilePower extends ActiveCooldownPowerFactory.Simple<FireProjectileConfiguration> {
	public FireProjectilePower() {
		super(FireProjectileConfiguration.CODEC);
	}

	@Override
	protected void execute(ConfiguredPower<FireProjectileConfiguration, ?> configuration, PlayerEntity player) {
		configuration.getConfiguration().fireProjectiles(player);
	}
}
