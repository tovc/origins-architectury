package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.INightVisionPower;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.power.TogglePowerFactory;
import io.github.apace100.origins.power.configuration.ToggleNightVisionConfiguration;
import net.minecraft.entity.player.PlayerEntity;

public class ToggleNightVisionPower extends TogglePowerFactory.Simple<ToggleNightVisionConfiguration> implements INightVisionPower<ToggleNightVisionConfiguration> {

	public ToggleNightVisionPower() {
		super(ToggleNightVisionConfiguration.CODEC);
	}

	@Override
	public float getStrength(ConfiguredPower<ToggleNightVisionConfiguration, ?> configuration, PlayerEntity player) {
		return configuration.getConfiguration().strength();
	}
}
