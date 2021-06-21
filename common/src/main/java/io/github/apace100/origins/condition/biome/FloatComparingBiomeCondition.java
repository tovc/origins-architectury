package io.github.apace100.origins.condition.biome;

import io.github.apace100.origins.api.configuration.FloatComparisonConfiguration;
import io.github.apace100.origins.api.power.factory.BiomeCondition;
import net.minecraft.world.biome.Biome;

import java.util.function.Function;

public class FloatComparingBiomeCondition extends BiomeCondition<FloatComparisonConfiguration> {
	private final Function<Biome, Float> function;

	public FloatComparingBiomeCondition(Function<Biome, Float> function) {
		super(FloatComparisonConfiguration.CODEC);
		this.function = function;
	}

	@Override
	protected boolean check(FloatComparisonConfiguration configuration, Biome biome) {
		return configuration.check(this.function.apply(biome));
	}
}
