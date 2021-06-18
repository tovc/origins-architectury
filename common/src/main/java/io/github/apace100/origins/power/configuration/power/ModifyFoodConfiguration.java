package io.github.apace100.origins.power.configuration.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemCondition;
import io.github.apace100.origins.power.configuration.ListConfiguration;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ModifyFoodConfiguration(ListConfiguration<EntityAttributeModifier> foodModifiers,
									  ListConfiguration<EntityAttributeModifier> saturationModifiers,
									  @Nullable ConfiguredItemCondition<?, ?> itemCondition,
									  @Nullable ConfiguredEntityAction<?, ?> entityAction) implements IOriginsFeatureConfiguration {
	public static final Codec<ModifyFoodConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.modifierCodec("food_modifier").forGetter(ModifyFoodConfiguration::foodModifiers),
			ListConfiguration.modifierCodec("saturation_modifier").forGetter(ModifyFoodConfiguration::saturationModifiers),
			ConfiguredItemCondition.CODEC.optionalFieldOf("item_condition").forGetter(x -> Optional.ofNullable(x.itemCondition())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("entity_action").forGetter(x -> Optional.ofNullable(x.entityAction()))
	).apply(instance, (t1, t2, t3, t4) -> new ModifyFoodConfiguration(t1, t2, t3.orElse(null), t4.orElse(null))));
}
