package io.github.apace100.origins.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.IntegerComparisonConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AdjacentConfiguration(IntegerComparisonConfiguration comparison,
									ConfiguredBlockCondition<?, ?> condition) implements IOriginsFeatureConfiguration {
	public static final Codec<AdjacentConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			IntegerComparisonConfiguration.MAP_CODEC.forGetter(AdjacentConfiguration::comparison),
			ConfiguredBlockCondition.CODEC.fieldOf("adjacent_condition").forGetter(AdjacentConfiguration::condition)
	).apply(instance, AdjacentConfiguration::new));

	@Override
	public boolean isConfigurationValid() {
		return this.condition().isConfigurationValid();
	}
}
