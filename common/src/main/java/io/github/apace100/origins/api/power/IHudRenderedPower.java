package io.github.apace100.origins.api.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.util.HudRender;
import net.minecraft.entity.player.PlayerEntity;

public interface IHudRenderedPower<T extends IOriginsFeatureConfiguration> {
	HudRender getRenderSettings(T configuration, PlayerEntity player);

	float getFill(T configuration, PlayerEntity player);

	boolean shouldRender(T configuration, PlayerEntity player);

	default HudRender getRenderSettings(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return this.getRenderSettings(configuration.getConfiguration(), player);
	}

	default float getFill(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return this.getFill(configuration.getConfiguration(), player);
	}

	default boolean shouldRender(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return this.shouldRender(configuration, player);
	}
}
