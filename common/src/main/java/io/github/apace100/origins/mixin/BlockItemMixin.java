package io.github.apace100.origins.mixin;

import io.github.apace100.origins.power.factories.PreventItemActionPower;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public class BlockItemMixin {

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;"), method = "useOnBlock")
	private TypedActionResult<ItemStack> preventItemUseIfBlockItem(BlockItem blockItem, World world, PlayerEntity user, Hand hand) {
		if (user != null) {
			ItemStack stackInHand = user.getStackInHand(hand);
			if (PreventItemActionPower.isUsagePrevented(user, stackInHand))
				return TypedActionResult.fail(stackInHand);
		}
		return blockItem.use(world, user, hand);
	}
}