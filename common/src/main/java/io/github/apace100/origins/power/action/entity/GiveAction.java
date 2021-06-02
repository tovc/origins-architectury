package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.Optional;
import java.util.function.Consumer;

public class GiveAction implements Consumer<Entity> {

	public static final Codec<GiveAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.ITEM_STACK.fieldOf("stack").forGetter(x -> Optional.of(x.stack).filter(y -> !y.isEmpty()))
	).apply(instance, GiveAction::new));

	private final ItemStack stack;

	public GiveAction(Optional<ItemStack> stack) {this.stack = stack.orElse(ItemStack.EMPTY);}

	@Override
	public void accept(Entity entity) {
		if(!entity.world.isClient()) {
			ItemStack stack = this.stack.copy();
			if(entity instanceof PlayerEntity)
				((PlayerEntity)entity).inventory.offerOrDrop(entity.world, stack);
			else
				entity.world.spawnEntity(new ItemEntity(entity.world, entity.getX(), entity.getY(), entity.getZ(), stack));
		}
	}
}
