package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ActionOverItemConfiguration(@Nullable ConfiguredEntityAction<?, ?> entityAction,
										  @Nullable ConfiguredEntityAction<?, ?> risingAction,
										  @Nullable ConfiguredEntityAction<?, ?> fallingAction,
										  int interval) implements IOriginsFeatureConfiguration {
	public static final Codec<ActionOverItemConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredEntityAction.CODEC.optionalFieldOf("entity_action").forGetter(x -> Optional.ofNullable(x.entityAction())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("rising_action").forGetter(x -> Optional.ofNullable(x.risingAction())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("falling_action").forGetter(x -> Optional.ofNullable(x.fallingAction())),
			Codec.INT.fieldOf("interval").forGetter(ActionOverItemConfiguration::interval)
	).apply(instance, (cea, cea2, cea3, interval) ->
			new ActionOverItemConfiguration(cea.orElse(null), cea2.orElse(null), cea3.orElse(null), interval)));
}
