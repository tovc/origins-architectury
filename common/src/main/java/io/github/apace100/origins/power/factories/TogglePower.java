package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.power.factory.power.TogglePowerFactory;
import io.github.apace100.origins.power.configuration.power.ToggleConfiguration;

public class TogglePower extends TogglePowerFactory.Simple<ToggleConfiguration> {

	public TogglePower() {
		super(ToggleConfiguration.CODEC);
	}
}
