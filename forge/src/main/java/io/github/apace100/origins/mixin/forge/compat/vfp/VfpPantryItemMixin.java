package io.github.apace100.origins.mixin.forge.compat.vfp;

import io.github.apace100.origins.power.ModifyFoodPower;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.util.AttributeUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jwaresoftware.mcmods.vfp.common.VfpPantryItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = VfpPantryItem.class)
public class VfpPantryItemMixin {

	@ModifyArgs(method = "onPortionConsumed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;add(IF)V"))
	private void origin$alterFood(Args args, ItemStack eaten, int foodAmount, float foodSaturation, World world, PlayerEntity player) {
		int foodArg = args.size() - 2;
		int satArg = args.size() - 1;
		if (player != null) {
			List<ModifyFoodPower> powers = ModComponentsArchitectury.getOriginComponent(player).getPowers(ModifyFoodPower.class).stream().filter(p -> p.doesApply(eaten)).collect(Collectors.toList());
			args.set(foodArg, (int) AttributeUtil.sortAndApplyModifiers(powers.stream().flatMap(x -> x.getFoodModifiers().stream()).collect(Collectors.toList()), args.<Integer>get(foodArg)));
			args.set(satArg, (float) AttributeUtil.sortAndApplyModifiers(powers.stream().flatMap(x -> x.getSaturationModifiers().stream()).collect(Collectors.toList()), args.<Float>get(satArg)));
		}
	}
}
