package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.BurnConfiguration;
import net.minecraft.entity.player.PlayerEntity;

public class BurnPower extends PowerFactory<BurnConfiguration> {
	public BurnPower() {
		super(BurnConfiguration.CODEC);
		this.ticking();
	}

	@Override
	protected void tick(BurnConfiguration configuration, PlayerEntity player) {
		player.setOnFireFor(configuration.duration());
	}

	@Override
	protected int tickInterval(BurnConfiguration configuration, PlayerEntity player) {
		return configuration.interval();
	}
}
