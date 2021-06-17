package io.github.apace100.origins.api.power.configuration.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

public interface IVariableIntPowerConfiguration extends IOriginsFeatureConfiguration {
	int min();
	int max();
	int initialValue();
}
