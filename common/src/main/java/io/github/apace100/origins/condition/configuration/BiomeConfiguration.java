package io.github.apace100.origins.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.ListConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredBiomeCondition;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record BiomeConfiguration(ListConfiguration<RegistryKey<Biome>> biomes,
								 @Nullable ConfiguredBiomeCondition<?, ?> condition) implements IOriginsFeatureConfiguration {

	public static final Codec<BiomeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.mapCodec(OriginsCodecs.BIOME, "biome", "biomes").forGetter(BiomeConfiguration::biomes),
			ConfiguredBiomeCondition.CODEC.optionalFieldOf("condition").forGetter(x -> Optional.ofNullable(x.condition()))
	).apply(instance, (t1, t2) -> new BiomeConfiguration(t1, t2.orElse(null))));
}
