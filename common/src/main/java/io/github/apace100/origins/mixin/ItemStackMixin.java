package io.github.apace100.origins.mixin;

import io.github.apace100.origins.power.factories.ActionOnItemUsePower;
import io.github.apace100.origins.power.factories.PreventItemActionPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

	@Unique
	private ItemStack usedItemStack;

	@Inject(at = @At("HEAD"), method = "use", cancellable = true)
	public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
		if (user != null) {
			ItemStack stackInHand = user.getStackInHand(hand);
			if (PreventItemActionPower.isUsagePrevented(user, stackInHand))
				info.setReturnValue(TypedActionResult.fail(stackInHand));
		}
	}

	@Inject(method = "finishUsing", at = @At("HEAD"))
	public void cacheUsedItemStack(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
		usedItemStack = ((ItemStack) (Object) this).copy();
	}

	@Inject(method = "finishUsing", at = @At("RETURN"))
	public void callActionOnUse(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
		if (user instanceof PlayerEntity) {
			ItemStack returnStack = cir.getReturnValue();
			ActionOnItemUsePower.execute((PlayerEntity) user, this.usedItemStack, returnStack);
		}
	}
}
