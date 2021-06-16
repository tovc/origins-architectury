package io.github.apace100.origins.api.power.factory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.IConditionFactory;
import io.github.apace100.origins.api.power.configuration.ConfiguredBiomeCondition;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import me.shedaniel.architectury.core.RegistryEntry;
import net.minecraft.world.biome.Biome;

public abstract class BiomeCondition<T extends IOriginsFeatureConfiguration> extends RegistryEntry<BiomeCondition<?>> implements IConditionFactory<T, ConfiguredBiomeCondition<T>, BiomeCondition<T>> {
	public static final Codec<BiomeCondition<?>> CODEC = OriginsRegistries.codec(OriginsRegistries.BIOME_CONDITION);

	private final Codec<Pair<T, ConditionData>> codec;

	protected BiomeCondition(Codec<T> codec) {
		this.codec = IConditionFactory.conditionCodec(codec);
	}

	@Override
	public Codec<Pair<T, ConditionData>> getConditionCodec() {
		return this.codec;
	}

	@Override
	public ConfiguredBiomeCondition<T> configure(T input, ConditionData configuration) {
		return new ConfiguredBiomeCondition<>(this, input, configuration);
	}

	protected boolean check(T configuration, Biome biome) {
		return false;
	}

	public boolean check(T configuration, ConditionData data, Biome biome) {
		return data.inverted() ^ this.check(configuration, biome);
	}
}
