package io.github.apace100.origins.api.power.configuration.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.IActivePower;

public interface ITogglePowerConfiguration extends IOriginsFeatureConfiguration {
	boolean defaultState();

	IActivePower.Key key();
}
