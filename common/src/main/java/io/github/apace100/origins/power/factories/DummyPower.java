package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.NoConfiguration;

public class DummyPower extends PowerFactory<NoConfiguration> {
	public DummyPower() {
		super(NoConfiguration.CODEC);
	}
}
