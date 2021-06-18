package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.power.factory.power.ValueModifyingPowerFactory;
import io.github.apace100.origins.power.configuration.power.ModifyBreakSpeedConfiguration;

public class ModifyBreakSpeedPower extends ValueModifyingPowerFactory<ModifyBreakSpeedConfiguration> {
	public ModifyBreakSpeedPower() {
		super(ModifyBreakSpeedConfiguration.CODEC);
	}
}
