package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;

public class EquippedItemAction implements Consumer<Entity> {
	public static final Codec<EquippedItemAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.EQUIPMENT_SLOT.fieldOf("equipment_slot").forGetter(x -> x.equipmentSlot),
			OriginsCodecs.ITEM_ACTION.fieldOf("action").forGetter(x -> x.action)
	).apply(instance, EquippedItemAction::new));

	private EquipmentSlot equipmentSlot;
	private ActionFactory.Instance<ItemStack> action;

	public EquippedItemAction(EquipmentSlot equipmentSlot, ActionFactory.Instance<ItemStack> action) {
		this.equipmentSlot = equipmentSlot;
		this.action = action;
	}

	@Override
	public void accept(Entity entity) {
		if(entity instanceof LivingEntity) {
			ItemStack stack = ((LivingEntity)entity).getEquippedStack(equipmentSlot);
			action.accept(stack);
		}
	}
}
