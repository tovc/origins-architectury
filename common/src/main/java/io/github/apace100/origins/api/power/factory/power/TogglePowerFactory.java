package io.github.apace100.origins.api.power.factory.power;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.IActivePower;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.configuration.power.ITogglePowerConfiguration;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class TogglePowerFactory<T extends ITogglePowerConfiguration> extends PowerFactory<T> implements IActivePower<T> {
	protected TogglePowerFactory(Codec<T> codec) {
		super(codec);
	}

	protected TogglePowerFactory(Codec<T> codec, boolean allowConditions) {
		super(codec, allowConditions);
	}

	protected abstract void setStatus(ConfiguredPower<T, ?> configuration, PlayerEntity player, boolean status);
	protected abstract boolean getStatus(ConfiguredPower<T, ?> configuration, PlayerEntity player);

	@Override
	public boolean isActive(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return super.isActive(configuration, player) && this.getStatus(configuration, player);
	}

	@Override
	public void activate(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		this.setStatus(configuration, player, !this.getStatus(configuration, player));
		OriginComponent.sync(player);
	}

	@Override
	public Key getKey(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return configuration.getConfiguration().key();
	}

	public static abstract class Simple<T extends ITogglePowerConfiguration> extends TogglePowerFactory<T> {
		protected Simple(Codec<T> codec) {
			super(codec);
		}

		protected Simple(Codec<T> codec, boolean allowConditions) {
			super(codec, allowConditions);
		}

		@Override
		protected void setStatus(ConfiguredPower<T, ?> configuration, PlayerEntity player, boolean status) {
			configuration.getPowerData(player, () -> new AtomicBoolean(configuration.getConfiguration().defaultState())).set(status);
		}

		@Override
		protected boolean getStatus(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
			return configuration.getPowerData(player, () -> new AtomicBoolean(configuration.getConfiguration().defaultState())).get();
		}

		@Override
		public Tag serialize(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
			return ByteTag.of(this.getStatus(configuration, player));
		}

		@Override
		public void deserialize(ConfiguredPower<T, ?> configuration, PlayerEntity player, Tag tag) {
			if (tag instanceof ByteTag byteTag)
				this.setStatus(configuration, player, byteTag.getByte() != 0);
		}
	}
}
