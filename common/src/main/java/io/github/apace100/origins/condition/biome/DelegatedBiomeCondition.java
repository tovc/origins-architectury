package io.github.apace100.origins.condition.biome;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.factory.BiomeCondition;
import io.github.apace100.origins.condition.meta.IDelegatedConditionConfiguration;
import net.minecraft.world.biome.Biome;

public class DelegatedBiomeCondition<T extends IDelegatedConditionConfiguration<Biome>> extends BiomeCondition<T> {
	public DelegatedBiomeCondition(Codec<T> codec) {
		super(codec);
	}

	@Override
	protected boolean check(T configuration, Biome biome) {
		return configuration.check(biome);
	}
}
