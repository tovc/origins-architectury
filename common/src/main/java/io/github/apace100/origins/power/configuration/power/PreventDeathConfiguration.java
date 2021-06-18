package io.github.apace100.origins.power.configuration.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredDamageCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record PreventDeathConfiguration(@Nullable ConfiguredEntityAction<?, ?> action,
										@Nullable ConfiguredDamageCondition<?, ?> condition) implements IOriginsFeatureConfiguration {
	public static final Codec<PreventDeathConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredEntityAction.CODEC.optionalFieldOf("entity_action").forGetter(x -> Optional.ofNullable(x.action)),
			ConfiguredDamageCondition.CODEC.optionalFieldOf("damage_condition").forGetter(x -> Optional.ofNullable(x.condition))
	).apply(instance, (action, condition) -> new PreventDeathConfiguration(action.orElse(null), condition.orElse(null))));
}
