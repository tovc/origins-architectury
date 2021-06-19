package io.github.apace100.origins.condition.biome;

import com.mojang.serialization.Codec;
import net.minecraft.world.biome.Biome;

import java.util.function.Predicate;

public class HighHumidityCondition implements Predicate<Biome> {

	public static final Codec<HighHumidityCondition> CODEC = Codec.unit(new HighHumidityCondition());

	@Override
	public boolean test(Biome biome) {
		return biome.hasHighHumidity();
	}
}
