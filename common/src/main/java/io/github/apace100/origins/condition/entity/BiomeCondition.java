package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.power.configuration.ConfiguredBiomeCondition;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.condition.configuration.BiomeConfiguration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.biome.Biome;

//FIXME This is wrong, it should be using RegistryKey instead.
public class BiomeCondition extends EntityCondition<BiomeConfiguration> {

	public BiomeCondition() {
		super(BiomeConfiguration.CODEC);
	}

	@Override
	public boolean check(BiomeConfiguration configuration, LivingEntity entity) {
		Biome biome = entity.world.getBiome(entity.getBlockPos());
		if (!ConfiguredBiomeCondition.check(configuration.condition(), biome))
			return false;
		return entity.world.getBiomeKey(entity.getBlockPos()).map(x -> configuration.biomes().getContent().stream().anyMatch(x::equals)).orElse(false);
	}
}
