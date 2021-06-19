package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ActionOnCallbackConfiguration(@Nullable ConfiguredEntityAction<?, ?> entityActionRespawned,
											@Nullable ConfiguredEntityAction<?, ?> entityActionRemoved,
											@Nullable ConfiguredEntityAction<?, ?> entityActionChosen,
											@Nullable ConfiguredEntityAction<?, ?> entityActionLost,
											@Nullable ConfiguredEntityAction<?, ?> entityActionAdded,
											boolean executeChosenWhenOrb) implements IOriginsFeatureConfiguration {
	public static final Codec<ActionOnCallbackConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredEntityAction.CODEC.optionalFieldOf("entity_action_respawned").forGetter(x -> Optional.ofNullable(x.entityActionRespawned())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("entity_action_removed").forGetter(x -> Optional.ofNullable(x.entityActionRemoved())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("entity_action_chosen").forGetter(x -> Optional.ofNullable(x.entityActionChosen())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("entity_action_lost").forGetter(x -> Optional.ofNullable(x.entityActionLost())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("entity_action_added").forGetter(x -> Optional.ofNullable(x.entityActionAdded())),
			Codec.BOOL.optionalFieldOf("execute_chosen_when_orb", true).forGetter(ActionOnCallbackConfiguration::executeChosenWhenOrb)
	).apply(instance, (cea, cea2, cea3, cea4, cea5, echo) ->
			new ActionOnCallbackConfiguration(cea.orElse(null), cea2.orElse(null), cea3.orElse(null), cea4.orElse(null), cea5.orElse(null), echo)));
}
