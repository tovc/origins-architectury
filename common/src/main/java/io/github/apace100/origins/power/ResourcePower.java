package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.power.HudRenderedVariableIntPowerFactory;
import io.github.apace100.origins.power.configuration.ResourceConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class ResourcePower extends HudRenderedVariableIntPowerFactory.Simple<ResourceConfiguration> {

	public ResourcePower() {
		super(ResourceConfiguration.CODEC);
	}

	@Override
	public int assign(ConfiguredPower<ResourceConfiguration, ?> configuration, PlayerEntity player, int value) {
		int previous = this.get(configuration, player);
		int minimum = this.getMinimum(configuration, player);
		int maximum = this.getMaximum(configuration, player);
		value = MathHelper.clamp(value, minimum, maximum);
		this.set(configuration, player, value);
		ResourceConfiguration config = configuration.getConfiguration();

		if (previous != value) {
			if (value == minimum) ConfiguredEntityAction.execute(config.minAction(), player);
			if (value == maximum) ConfiguredEntityAction.execute(config.maxAction(), player);
		}
		return value;
	}
}
