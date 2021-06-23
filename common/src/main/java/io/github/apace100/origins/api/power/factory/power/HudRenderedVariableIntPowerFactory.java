package io.github.apace100.origins.api.power.factory.power;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.IHudRenderedPower;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.configuration.power.IHudRenderedVariableIntPowerConfiguration;
import io.github.apace100.origins.util.HudRender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class HudRenderedVariableIntPowerFactory<T extends IHudRenderedVariableIntPowerConfiguration> extends VariableIntPowerFactory<T> implements IHudRenderedPower<T> {
	protected HudRenderedVariableIntPowerFactory(Codec<T> codec) {
		super(codec);
	}

	protected HudRenderedVariableIntPowerFactory(Codec<T> codec, boolean allowConditions) {
		super(codec, allowConditions);
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
		return true;
	}

	public static abstract class Simple<T extends IHudRenderedVariableIntPowerConfiguration> extends HudRenderedVariableIntPowerFactory<T> {
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
