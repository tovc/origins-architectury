package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.factory.power.ValueModifyingPowerFactory;
import io.github.apace100.origins.power.configuration.ValueModifyingPowerConfiguration;

public class ModifyValuePower extends ValueModifyingPowerFactory<ValueModifyingPowerConfiguration> {
	public ModifyValuePower() {
		super(ValueModifyingPowerConfiguration.CODEC);
	}
}
