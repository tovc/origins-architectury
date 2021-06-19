package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.factory.power.ValueModifyingPowerFactory;
import io.github.apace100.origins.power.configuration.ModifyBreakSpeedConfiguration;

public class ModifyBreakSpeedPower extends ValueModifyingPowerFactory<ModifyBreakSpeedConfiguration> {
	public ModifyBreakSpeedPower() {
		super(ModifyBreakSpeedConfiguration.CODEC);
	}
}
