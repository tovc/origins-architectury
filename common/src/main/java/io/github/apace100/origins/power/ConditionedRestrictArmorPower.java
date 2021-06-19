package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.RestrictArmorConfiguration;
import net.minecraft.entity.player.PlayerEntity;

public class ConditionedRestrictArmorPower extends PowerFactory<RestrictArmorConfiguration> {
	public ConditionedRestrictArmorPower() {
		super(RestrictArmorConfiguration.CODEC);
		this.ticking();
	}

	@Override
	protected void tick(RestrictArmorConfiguration configuration, PlayerEntity player) {
		configuration.dropIllegalItems(player);
	}

	@Override
	protected int tickInterval(RestrictArmorConfiguration configuration, PlayerEntity player) {
		return configuration.tickRate();
	}
}
