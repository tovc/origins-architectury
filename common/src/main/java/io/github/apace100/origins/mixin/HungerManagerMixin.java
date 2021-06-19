package io.github.apace100.origins.mixin;

import io.github.apace100.origins.power.configuration.ModifyFoodConfiguration;
import io.github.apace100.origins.power.ModifyFoodPower;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {

	@Unique
	private PlayerEntity player;

	@Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/FoodComponent;getHunger()I"))
	private int modifyHunger(FoodComponent foodComponent, Item item, ItemStack stack) {
		if (player != null) {
			return (int) ModifyFoodPower.apply(this.player, stack, foodComponent.getHunger(), ModifyFoodConfiguration::foodModifiers);
		}
		return foodComponent.getHunger();
	}

	@Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/FoodComponent;getSaturationModifier()F"))
	private float modifySaturation(FoodComponent foodComponent, Item item, ItemStack stack) {
		if (player != null) {
			return (float) ModifyFoodPower.apply(this.player, stack, foodComponent.getSaturationModifier(), ModifyFoodConfiguration::saturationModifiers);
		}
		return foodComponent.getSaturationModifier();
	}

	@Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;add(IF)V", shift = At.Shift.AFTER))
	private void executeAdditionalEatAction(Item item, ItemStack stack, CallbackInfo ci) {
		if (player != null) {
			ModifyFoodPower.execute(this.player, stack);
		}
	}

	@Inject(method = "update", at = @At("HEAD"))
	private void cachePlayer(PlayerEntity player, CallbackInfo ci) {
		this.player = player;
	}
}
