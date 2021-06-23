package io.github.apace100.origins.api.power.factory.power;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.IVariableIntPower;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.configuration.power.IVariableIntPowerConfiguration;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class VariableIntPowerFactory<T extends IVariableIntPowerConfiguration> extends PowerFactory<T> implements IVariableIntPower<T> {
	protected VariableIntPowerFactory(Codec<T> codec) {
		super(codec);
	}

	protected VariableIntPowerFactory(Codec<T> codec, boolean allowConditions) {
		super(codec, allowConditions);
	}

	protected abstract int get(ConfiguredPower<T, ?> configuration, PlayerEntity player);

	protected abstract void set(ConfiguredPower<T, ?> configuration, PlayerEntity player, int value);

	@Override
	public int assign(ConfiguredPower<T, ?> configuration, PlayerEntity player, int value) {
		value = MathHelper.clamp(value, this.getMinimum(configuration, player), this.getMaximum(configuration, player));
		this.set(configuration, player, value);
		return value;
	}

	@Override
	public int getValue(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return this.get(configuration, player);
	}

	@Override
	public int getMaximum(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return configuration.getConfiguration().max();
	}

	@Override
	public int getMinimum(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return configuration.getConfiguration().min();
	}

	public static abstract class Simple<T extends IVariableIntPowerConfiguration> extends VariableIntPowerFactory<T> {
		protected Simple(Codec<T> codec) {
			super(codec);
		}

		protected Simple(Codec<T> codec, boolean allowConditions) {
			super(codec, allowConditions);
		}

		protected AtomicInteger getCurrentValue(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
			return configuration.getPowerData(player, () -> new AtomicInteger(configuration.getConfiguration().initialValue()));
		}

		@Override
		protected int get(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
			return this.getCurrentValue(configuration, player).get();
		}

		@Override
		protected void set(ConfiguredPower<T, ?> configuration, PlayerEntity player, int value) {
			this.getCurrentValue(configuration, player).set(value);
		}

		@Override
		public Tag serialize(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
			return IntTag.of(this.get(configuration, player));
		}

		@Override
		public void deserialize(ConfiguredPower<T, ?> configuration, PlayerEntity player, Tag tag) {
			if (tag instanceof IntTag intTag)
				this.set(configuration, player, intTag.getInt());
		}
	}
}
