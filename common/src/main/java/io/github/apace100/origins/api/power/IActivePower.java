package io.github.apace100.origins.api.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import net.minecraft.entity.player.PlayerEntity;

public interface IActivePower<T extends IOriginsFeatureConfiguration> {
	void onUse(T configuration, PlayerEntity player);

	IActivePower.Key getKey(T configuration, PlayerEntity player);

	default void onUse(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		this.onUse(configuration.getConfiguration(), player);
	}

	default IActivePower.Key getKey(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return this.getKey(configuration.getConfiguration(), player);
	}

	record Key(String key, boolean continuous) {
		public static final Key DEFAULT = new Key("key.conditionedOrigins.primary_active", false);
		//TODO Codec
	}
}
