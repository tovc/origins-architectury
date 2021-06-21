package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class GiveAction extends EntityAction<FieldConfiguration<Optional<ItemStack>>> {
	public GiveAction() {
		super(FieldConfiguration.codec(OriginsCodecs.ITEM_STACK, "stack"));
	}

	@Override
	public void execute(FieldConfiguration<Optional<ItemStack>> configuration, Entity entity) {
		configuration.value().ifPresent(x -> {
			if (!entity.world.isClient()) {
				ItemStack stack = x.copy();
				if (entity instanceof PlayerEntity player)
					player.inventory.offerOrDrop(entity.world, stack);
				else
					entity.world.spawnEntity(new ItemEntity(entity.world, entity.getX(), entity.getY(), entity.getZ(), stack));
			}
		});
	}
}
