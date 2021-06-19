package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.ActionOverItemConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActionOverTimePower extends PowerFactory<ActionOverItemConfiguration> {
	public ActionOverTimePower() {
		super(ActionOverItemConfiguration.CODEC);
		this.ticking(true);
	}

	@Override
	public void tick(ConfiguredPower<ActionOverItemConfiguration, ?> configuration, PlayerEntity player) {
		AtomicBoolean data = configuration.getPowerData(player, () -> new AtomicBoolean(false));
		ActionOverItemConfiguration config = configuration.getConfiguration();
		if (configuration.isActive(player)) {
			if (config.entityAction() != null)
				config.entityAction().execute(player);
			if (!data.get() && config.risingAction() != null)
				config.risingAction().execute(player);
			data.set(true);
		} else {
			if (data.get() && config.fallingAction() != null)
				config.fallingAction().execute(player);
			data.set(false);
		}
	}

	@Override
	protected int tickInterval(ActionOverItemConfiguration configuration, PlayerEntity player) {
		return configuration.interval();
	}

	@Override
	public Tag serialize(ConfiguredPower<ActionOverItemConfiguration, ?> configuration, PlayerEntity player) {
		return ByteTag.of(configuration.getPowerData(player, () -> new AtomicBoolean(false)).get());
	}

	@Override
	public void deserialize(ConfiguredPower<ActionOverItemConfiguration, ?> configuration, PlayerEntity player, Tag tag) {
		AtomicBoolean data = configuration.getPowerData(player, () -> new AtomicBoolean(false));
		data.set(!Objects.equals(tag, ByteTag.ZERO));
	}
}
