package io.github.apace100.origins.power.factories;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.factory.power.CooldownPowerFactory;
import io.github.apace100.origins.power.configuration.power.CooldownConfiguration;

public class CooldownPower extends CooldownPowerFactory.Simple<CooldownConfiguration> {
	public CooldownPower() {
		super(CooldownConfiguration.CODEC);
	}
}
