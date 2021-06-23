package io.github.apace100.origins.api.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import net.minecraft.entity.player.PlayerEntity;

public interface ICooldownPower<T extends IOriginsFeatureConfiguration> extends IVariableIntPower<T> {
	void use(ConfiguredPower<T, ?> configuration, PlayerEntity player);

	boolean canUse(ConfiguredPower<T, ?> configuration, PlayerEntity player);
}
