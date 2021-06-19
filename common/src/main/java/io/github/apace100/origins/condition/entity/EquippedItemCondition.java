package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class EquippedItemCondition implements Predicate<LivingEntity> {
	public static final Codec<EquippedItemCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.EQUIPMENT_SLOT.fieldOf("equipment_slot").forGetter(x -> x.slot),
			OriginsCodecs.ITEM_CONDITION.fieldOf("item_condition").forGetter(x -> x.itemCondition)
	).apply(instance, EquippedItemCondition::new));


	private final EquipmentSlot slot;
	private final ConditionFactory.Instance<ItemStack> itemCondition;

	public EquippedItemCondition(EquipmentSlot slot, ConditionFactory.Instance<ItemStack> itemCondition) {
		this.slot = slot;
		this.itemCondition = itemCondition;
	}

	@Override
	public boolean test(LivingEntity entity) {
		return this.itemCondition.test(entity.getEquippedStack(this.slot));
	}
}
