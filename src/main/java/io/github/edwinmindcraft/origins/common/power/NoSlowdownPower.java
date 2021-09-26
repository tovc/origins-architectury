package io.github.edwinmindcraft.origins.common.power;

import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.origins.common.power.configuration.NoSlowdownConfiguration;

public class NoSlowdownPower extends PowerFactory<NoSlowdownConfiguration> {
	public NoSlowdownPower() {
		super(NoSlowdownConfiguration.CODEC);
	}
}
