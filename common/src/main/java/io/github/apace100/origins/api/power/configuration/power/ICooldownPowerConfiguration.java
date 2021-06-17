package io.github.apace100.origins.api.power.configuration.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.util.HudRender;

public interface ICooldownPowerConfiguration extends IOriginsFeatureConfiguration {
	int duration();
	HudRender hudRender();
}
