package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.factory.power.CooldownPowerFactory;
import io.github.apace100.origins.power.configuration.CooldownConfiguration;

public class CooldownPower extends CooldownPowerFactory.Simple<CooldownConfiguration> {
	public CooldownPower() {
		super(CooldownConfiguration.CODEC);
	}
}
