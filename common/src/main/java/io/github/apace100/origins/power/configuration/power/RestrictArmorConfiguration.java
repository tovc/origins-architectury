package io.github.apace100.origins.power.configuration.power;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemCondition;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.Optional;

public record RestrictArmorConfiguration(Map<EquipmentSlot, ConfiguredItemCondition<?, ?>> conditions,
										 int tickRate) implements IOriginsFeatureConfiguration {
	private static final MapCodec<Map<EquipmentSlot, ConfiguredItemCondition<?, ?>>> EQUIPMENT_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ConfiguredItemCondition.CODEC.optionalFieldOf("head").forGetter(x -> Optional.ofNullable(x.get(EquipmentSlot.HEAD))),
			ConfiguredItemCondition.CODEC.optionalFieldOf("chest").forGetter(x -> Optional.ofNullable(x.get(EquipmentSlot.CHEST))),
			ConfiguredItemCondition.CODEC.optionalFieldOf("legs").forGetter(x -> Optional.ofNullable(x.get(EquipmentSlot.LEGS))),
			ConfiguredItemCondition.CODEC.optionalFieldOf("feet").forGetter(x -> Optional.ofNullable(x.get(EquipmentSlot.FEET)))
	).apply(instance, (head, chest, legs, feet) -> {
		ImmutableMap.Builder<EquipmentSlot, ConfiguredItemCondition<?, ?>> builder = ImmutableMap.builder();
		head.ifPresent(x -> builder.put(EquipmentSlot.HEAD, x));
		chest.ifPresent(x -> builder.put(EquipmentSlot.CHEST, x));
		legs.ifPresent(x -> builder.put(EquipmentSlot.LEGS, x));
		feet.ifPresent(x -> builder.put(EquipmentSlot.FEET, x));
		return builder.build();
	}));

	public static final Codec<RestrictArmorConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EQUIPMENT_MAP_CODEC.forGetter(RestrictArmorConfiguration::conditions),
			Codec.INT.optionalFieldOf("tick_rate", 80).forGetter(RestrictArmorConfiguration::tickRate)
	).apply(instance, RestrictArmorConfiguration::new));

	public boolean check(EquipmentSlot slot, ItemStack stack) {
		return ConfiguredItemCondition.check(this.conditions.get(slot), stack);
	}

	public void dropIllegalItems(PlayerEntity player) {
		this.conditions().forEach((slot, predicate) -> {
			if (predicate == null) return;
			ItemStack equippedItem = player.getEquippedStack(slot);
			if (!equippedItem.isEmpty() && !predicate.check(equippedItem)) {
				if (!player.inventory.insertStack(equippedItem))
					player.dropItem(equippedItem, true);
				player.equipStack(slot, ItemStack.EMPTY);
			}
		});
	}
}
