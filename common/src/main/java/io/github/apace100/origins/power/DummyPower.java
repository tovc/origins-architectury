package io.github.apace100.origins.power;

import io.github.apace100.origins.api.configuration.NoConfiguration;
import io.github.apace100.origins.api.power.factory.PowerFactory;

public class DummyPower extends PowerFactory<NoConfiguration> {
	public DummyPower() {
		super(NoConfiguration.CODEC);
	}
}
