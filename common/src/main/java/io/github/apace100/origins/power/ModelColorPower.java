package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.ColorConfiguration;

public class ModelColorPower extends PowerFactory<ColorConfiguration> {
	public ModelColorPower() {
		super(ColorConfiguration.CODEC);
	}
}
