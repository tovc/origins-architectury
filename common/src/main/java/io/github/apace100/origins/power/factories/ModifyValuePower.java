package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.power.factory.power.ValueModifyingPowerFactory;
import io.github.apace100.origins.power.configuration.power.ValueModifyingPowerConfiguration;

public class ModifyValuePower extends ValueModifyingPowerFactory<ValueModifyingPowerConfiguration> {
	public ModifyValuePower() {
		super(ValueModifyingPowerConfiguration.CODEC);
	}
}
