package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.power.ColorConfiguration;

public class ModelColorPower extends PowerFactory<ColorConfiguration> {
	public ModelColorPower() {
		super(ColorConfiguration.CODEC);
	}
}
