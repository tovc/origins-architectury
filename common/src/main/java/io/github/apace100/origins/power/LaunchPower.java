package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.power.ActiveCooldownPowerFactory;
import io.github.apace100.origins.power.configuration.LaunchConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class LaunchPower extends ActiveCooldownPowerFactory.Simple<LaunchConfiguration> {
	public LaunchPower() {
		super(LaunchConfiguration.CODEC);
	}

	@Override
	protected void execute(ConfiguredPower<LaunchConfiguration, ?> configuration, PlayerEntity player) {
		World world = player.getEntityWorld();
		if (!world.isClient) {
			LaunchConfiguration config = configuration.getConfiguration();
			player.addVelocity(0, config.speed(), 0);
			player.velocityModified = true;
			if (config.sound() != null)
				world.playSound(null, player.getX(), player.getY(), player.getZ(), config.sound(), SoundCategory.NEUTRAL, 0.5F, 0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F));
			if (player.world instanceof ServerWorld serverWorld) {
				for (int i = 0; i < 4; ++i)
					serverWorld.spawnParticles(ParticleTypes.CLOUD, player.getX(), player.getRandomBodyY(), player.getZ(), 8, player.getRandom().nextGaussian(), 0.0D, player.getRandom().nextGaussian(), 0.5);
			}
		}
	}
}
