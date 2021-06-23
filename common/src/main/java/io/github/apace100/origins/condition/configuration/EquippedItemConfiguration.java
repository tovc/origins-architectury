package io.github.apace100.origins.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemCondition;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.EquipmentSlot;

public record EquippedItemConfiguration(EquipmentSlot slot,
										ConfiguredItemCondition<?, ?> condition) implements IOriginsFeatureConfiguration {
	public static final Codec<EquippedItemConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.EQUIPMENT_SLOT.fieldOf("equipment_slot").forGetter(EquippedItemConfiguration::slot),
			ConfiguredItemCondition.CODEC.fieldOf("item_condition").forGetter(EquippedItemConfiguration::condition)
	).apply(instance, EquippedItemConfiguration::new));
}
