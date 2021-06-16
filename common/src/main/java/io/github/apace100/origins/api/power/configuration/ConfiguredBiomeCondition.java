package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.ConfiguredCondition;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.BiomeCondition;
import net.minecraft.world.biome.Biome;

import java.util.function.Function;

public final class ConfiguredBiomeCondition<T extends IOriginsFeatureConfiguration> extends ConfiguredCondition<T, BiomeCondition<T>> {
	public static final Codec<ConfiguredBiomeCondition<?>> CODEC = BiomeCondition.CODEC.dispatch(ConfiguredFactory::getFactory, Function.identity());

	public ConfiguredBiomeCondition(BiomeCondition<T> factory, T configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(Biome biome) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), biome);
	}
}