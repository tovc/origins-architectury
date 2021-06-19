package io.github.apace100.origins.api.power.factory.power;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.ICooldownPower;
import io.github.apace100.origins.api.power.IHudRenderedPower;
import io.github.apace100.origins.api.power.IVariableIntPower;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.api.power.configuration.power.ICooldownPowerConfiguration;
import io.github.apace100.origins.util.HudRender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.atomic.AtomicLong;

public abstract class CooldownPowerFactory<T extends ICooldownPowerConfiguration> extends PowerFactory<T> implements ICooldownPower<T>, IHudRenderedPower<T> {
	protected CooldownPowerFactory(Codec<T> codec) {
		super(codec);
	}

	protected CooldownPowerFactory(Codec<T> codec, boolean allowConditions) {
		super(codec, allowConditions);
	}

	@Override
	public boolean canUse(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return this.getRemainingDuration(configuration, player) <= 0 && configuration.isActive(player);
	}

	@Override
	public void use(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		this.setLastUseTime(configuration, player, player.getEntityWorld().getTime());
		OriginComponent.sync(player);
	}

	@Override
	public HudRender getRenderSettings(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return configuration.getConfiguration().hudRender();
	}

	@Override
	public float getFill(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return this.getProgress(configuration, player);
	}

	@Override
	public boolean shouldRender(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return this.getRemainingDuration(configuration, player) > 0;
	}

	protected abstract long getLastUseTime(ConfiguredPower<T, ?> configuration, PlayerEntity player);

	protected abstract void setLastUseTime(ConfiguredPower<T, ?> configuration, PlayerEntity player, long value);

	protected long getElapsedDuration(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return Math.max(player.getEntityWorld().getTime() - getLastUseTime(configuration, player), 0);
	}

	protected long getRemainingDuration(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return Math.max(getLastUseTime(configuration, player) - player.getEntityWorld().getTime(), 0);
	}

	@Override
	public int assign(ConfiguredPower<T, ?> configuration, PlayerEntity player, int value) {
		value = MathHelper.clamp(value, this.getMaximum(configuration, player), this.getMaximum(configuration, player));
		this.setLastUseTime(configuration, player, player.getEntityWorld().getTime() - value);
		return value;
	}

	@Override
	public int getValue(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return Math.toIntExact(MathHelper.clamp(this.getRemainingDuration(configuration, player), this.getMaximum(configuration, player), this.getMaximum(configuration, player)));
	}

	@Override
	public int getMaximum(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return configuration.getConfiguration().duration();
	}

	@Override
	public int getMinimum(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return 0;
	}

	/**
	 * A partial implementation of {@link CooldownPowerFactory} with default serialization functions.
	 */
	public static abstract class Simple<T extends ICooldownPowerConfiguration> extends CooldownPowerFactory<T> {
		protected Simple(Codec<T> codec) {
			super(codec);
		}

		protected Simple(Codec<T> codec, boolean allowConditions) {
			super(codec, allowConditions);
		}

		protected AtomicLong getUseTime(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
			return configuration.getPowerData(player, () -> new AtomicLong(Integer.MIN_VALUE));
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
