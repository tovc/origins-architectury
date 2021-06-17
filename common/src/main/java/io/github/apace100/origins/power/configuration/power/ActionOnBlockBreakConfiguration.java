package io.github.apace100.origins.power.configuration.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ActionOnBlockBreakConfiguration(@Nullable ConfiguredBlockCondition<?, ?> blockCondition,
											  @Nullable ConfiguredEntityAction<?, ?> entityAction,
											  @Nullable ConfiguredBlockAction<?, ?> blockAction,
											  boolean onlyWhenHarvested) implements IOriginsFeatureConfiguration {

	public static final Codec<ActionOnBlockBreakConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredBlockCondition.CODEC.optionalFieldOf("block_condition").forGetter(x -> Optional.ofNullable(x.blockCondition())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("entity_action").forGetter(x -> Optional.ofNullable(x.entityAction())),
			ConfiguredBlockAction.CODEC.optionalFieldOf("block_action").forGetter(x -> Optional.ofNullable(x.blockAction())),
			Codec.BOOL.optionalFieldOf("only_when_harvested", true).forGetter(ActionOnBlockBreakConfiguration::onlyWhenHarvested)
	).apply(instance, (bc, ea, ba, owh) -> new ActionOnBlockBreakConfiguration(bc.orElse(null), ea.orElse(null), ba.orElse(null), owh)));
}
