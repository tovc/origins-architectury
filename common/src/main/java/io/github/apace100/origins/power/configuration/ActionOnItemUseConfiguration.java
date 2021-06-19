package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemCondition;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ActionOnItemUseConfiguration(@Nullable ConfiguredItemCondition<?, ?> itemCondition,
										   @Nullable ConfiguredEntityAction<?, ?> entityAction,
										   @Nullable ConfiguredItemAction<?, ?> itemAction) implements IOriginsFeatureConfiguration {

	public static final Codec<ActionOnItemUseConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredItemCondition.CODEC.optionalFieldOf("item_condition").forGetter(x -> Optional.ofNullable(x.itemCondition())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("entity_action").forGetter(x -> Optional.ofNullable(x.entityAction())),
			ConfiguredItemAction.CODEC.optionalFieldOf("item_action").forGetter(x -> Optional.ofNullable(x.itemAction()))
	).apply(instance, (o1, o2, o3) -> new ActionOnItemUseConfiguration(o1.orElse(null), o2.orElse(null), o3.orElse(null))));
}
