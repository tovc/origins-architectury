package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.factory.power.TogglePowerFactory;
import io.github.apace100.origins.power.configuration.ToggleConfiguration;

public class TogglePower extends TogglePowerFactory.Simple<ToggleConfiguration> {

	public TogglePower() {
		super(ToggleConfiguration.CODEC);
	}
}
