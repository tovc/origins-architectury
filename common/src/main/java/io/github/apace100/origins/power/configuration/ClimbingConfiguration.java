package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ClimbingConfiguration(boolean allowHolding, @Nullable ConfiguredEntityCondition<?, ?> condition) implements IOriginsFeatureConfiguration {
	public static final Codec<ClimbingConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("allow_holding", true).forGetter(ClimbingConfiguration::allowHolding),
			ConfiguredEntityCondition.CODEC.optionalFieldOf("hold_condition").forGetter(x -> Optional.ofNullable(x.condition()))
	).apply(instance, (t1, t2) -> new ClimbingConfiguration(t1, t2.orElse(null))));
}
