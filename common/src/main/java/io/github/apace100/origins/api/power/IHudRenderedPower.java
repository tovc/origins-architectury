package io.github.apace100.origins.api.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.util.HudRender;
import net.minecraft.entity.player.PlayerEntity;

public interface IHudRenderedPower<T extends IOriginsFeatureConfiguration> {

	HudRender getRenderSettings(ConfiguredPower<T, ?> configuration, PlayerEntity player);

	float getFill(ConfiguredPower<T, ?> configuration, PlayerEntity player);

	boolean shouldRender(ConfiguredPower<T, ?> configuration, PlayerEntity player);
}
