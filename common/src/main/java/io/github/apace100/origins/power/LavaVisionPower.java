package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.LavaVisionConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.Entity;

import java.util.Optional;

public class LavaVisionPower extends PowerFactory<LavaVisionConfiguration> {
	public static Optional<Float> getS(Entity entity) {
		return OriginComponent.getPowers(entity, ModPowers.LAVA_VISION.get()).stream().map(ConfiguredFactory::getConfiguration).map(LavaVisionConfiguration::s).findFirst();
	}

	public static Optional<Float> getV(Entity entity) {
		return OriginComponent.getPowers(entity, ModPowers.LAVA_VISION.get()).stream().map(ConfiguredFactory::getConfiguration).map(LavaVisionConfiguration::s).findFirst();
	}

	public LavaVisionPower() {
		super(LavaVisionConfiguration.CODEC);
	}
}
