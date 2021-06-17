package io.github.apace100.origins.api.power.factory.power;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.IActivePower;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.configuration.power.IActiveCooldownPowerConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;

import java.util.concurrent.atomic.AtomicLong;

public abstract class ActiveCooldownPowerFactory<T extends IActiveCooldownPowerConfiguration> extends CooldownPowerFactory<T> implements IActivePower<T> {
	protected ActiveCooldownPowerFactory(Codec<T> codec) {
		super(codec);
	}

	protected ActiveCooldownPowerFactory(Codec<T> codec, boolean allowConditions) {
		super(codec, allowConditions);
	}

	protected abstract void execute(ConfiguredPower<T, ?> configuration, PlayerEntity player);

	@Override
	public void activate(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		if (this.canUse(configuration, player)) {
			this.execute(configuration, player);
			this.use(configuration, player);
		}
	}

	@Override
	public Key getKey(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return configuration.getConfiguration().key();
	}

	/**
	 * A partial implementation of {@link ActiveCooldownPowerFactory} with default serialization functions.
	 */
	public static abstract class Simple<T extends IActiveCooldownPowerConfiguration> extends ActiveCooldownPowerFactory<T> {
		protected Simple(Codec<T> codec) {
			super(codec);
		}

		protected Simple(Codec<T> codec, boolean allowConditions) {
			super(codec, allowConditions);
		}

		protected AtomicLong getUseTime(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
			return configuration.getPowerData(player, () -> new AtomicLong(Long.MIN_VALUE));
		}

		@Override
		protected long getLastUseTime(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
			return this.getUseTime(configuration, player).get();
		}

		@Override
		protected void setLastUseTime(ConfiguredPower<T, ?> configuration, PlayerEntity player, long value) {
			this.getUseTime(configuration, player).set(value);
		}

		@Override
		public Tag serialize(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
			return LongTag.of(this.getLastUseTime(configuration, player));
		}

		@Override
		public void deserialize(ConfiguredPower<T, ?> configuration, PlayerEntity player, Tag tag) {
			if (tag instanceof LongTag longTag)
				this.setLastUseTime(configuration, player, longTag.getLong());
		}
	}
}
