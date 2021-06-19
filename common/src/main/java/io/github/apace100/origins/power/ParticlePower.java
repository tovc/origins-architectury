package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.ParticleConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;

public class ParticlePower extends PowerFactory<ParticleConfiguration> {

	@Environment(EnvType.CLIENT)
	public static void renderParticles(PlayerEntity player) {
		OriginComponent.getPowers(player, ModPowers.PARTICLE.get()).stream().filter(x -> player.age % x.getConfiguration().frequency() == 0)
				.forEach(power -> player.world.addParticle(power.getConfiguration().particle(), player.getParticleX(0.5), player.getRandomBodyY(), player.getParticleZ(0.5), 0, 0, 0));
	}

	public ParticlePower() {
		super(ParticleConfiguration.CODEC);
	}
}
