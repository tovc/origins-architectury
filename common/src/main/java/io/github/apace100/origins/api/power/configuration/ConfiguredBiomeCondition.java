package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.ConfiguredCondition;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.BiomeCondition;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ConfiguredBiomeCondition<C extends IOriginsFeatureConfiguration, F extends BiomeCondition<C>> extends ConfiguredCondition<C, F> {
	public static final Codec<ConfiguredBiomeCondition<?, ?>> CODEC = BiomeCondition.CODEC.dispatch(ConfiguredFactory::getFactory, Function.identity());

	public static boolean check(@Nullable ConfiguredBiomeCondition<?, ?> condition, Biome biome) {
		return condition == null || condition.check(biome);
	}

	public ConfiguredBiomeCondition(F factory, C configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(Biome biome) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), biome);
	}
}