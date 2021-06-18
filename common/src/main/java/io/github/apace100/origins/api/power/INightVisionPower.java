package io.github.apace100.origins.api.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

public interface INightVisionPower<T extends IOriginsFeatureConfiguration> {
	@SuppressWarnings({"rawtypes", "unchecked"})
	static Optional<Float> getNightVisionStrength(PlayerEntity player) {
		return OriginsAPI.getComponent(player).getPowers().stream().filter(x -> x.isActive(player) && x.getFactory() instanceof INightVisionPower).map(x -> getValue((ConfiguredPower) x, player)).max(Float::compareTo);
	}

	private static <T extends IOriginsFeatureConfiguration, F extends PowerFactory<T> & INightVisionPower<T>> float getValue(ConfiguredPower<T, F> configuration, PlayerEntity player) {
		return configuration.getFactory().getStrength(configuration, player);
	}

	float getStrength(ConfiguredPower<T, ?> configuration, PlayerEntity player);
}
