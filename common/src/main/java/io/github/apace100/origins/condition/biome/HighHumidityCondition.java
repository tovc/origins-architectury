package io.github.apace100.origins.condition.biome;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.configuration.NoConfiguration;
import io.github.apace100.origins.api.power.factory.BiomeCondition;
import net.minecraft.world.biome.Biome;

public class HighHumidityCondition extends BiomeCondition<NoConfiguration> {

	public static final Codec<HighHumidityCondition> CODEC = Codec.unit(new HighHumidityCondition());

	public HighHumidityCondition() {
		super(NoConfiguration.CODEC);
	}

	@Override
	protected boolean check(NoConfiguration configuration, Biome biome) {
		return biome.hasHighHumidity();
	}
}
