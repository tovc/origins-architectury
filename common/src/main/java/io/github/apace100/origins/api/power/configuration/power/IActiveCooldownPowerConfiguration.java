package io.github.apace100.origins.api.power.configuration.power;

import io.github.apace100.origins.api.power.IActivePower;

public interface IActiveCooldownPowerConfiguration extends ICooldownPowerConfiguration {
	IActivePower.Key key();
}
