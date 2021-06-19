package io.github.apace100.origins.action.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemAction;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.EquipmentSlot;

public record EquippedItemConfiguration(EquipmentSlot slot, ConfiguredItemAction<?, ?> action) implements IOriginsFeatureConfiguration {

	public static final Codec<EquippedItemConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.EQUIPMENT_SLOT.fieldOf("equipment_slot").forGetter(EquippedItemConfiguration::slot),
			ConfiguredItemAction.CODEC.fieldOf("action").forGetter(EquippedItemConfiguration::action)
	).apply(instance, EquippedItemConfiguration::new));
}
