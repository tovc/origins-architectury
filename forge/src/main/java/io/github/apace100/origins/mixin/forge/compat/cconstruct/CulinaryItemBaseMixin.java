package io.github.apace100.origins.mixin.forge.compat.cconstruct;


import io.github.apace100.origins.power.ModifyFoodPower;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.util.AttributeUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import top.theillusivec4.culinaryconstruct.common.item.CulinaryItemBase;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(CulinaryItemBase.class)
public class CulinaryItemBaseMixin {

	@ModifyArgs(method = "finishUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;add(IF)V"))
	private void origin$alterFood(Args args, ItemStack stack, World worldIn, LivingEntity living) {
		int foodArg = args.size() - 2;
		int satArg = args.size() - 1;
		if (living != null) {
			List<ModifyFoodPower> powers = ModComponentsArchitectury.getOriginComponent(living).getPowers(ModifyFoodPower.class).stream().filter(p -> p.doesApply(stack)).collect(Collectors.toList());
			args.set(foodArg, (int) AttributeUtil.sortAndApplyModifiers(powers.stream().flatMap(x -> x.getFoodModifiers().stream()).collect(Collectors.toList()), args.<Integer>get(foodArg)));
			args.set(satArg, (float) AttributeUtil.sortAndApplyModifiers(powers.stream().flatMap(x -> x.getSaturationModifiers().stream()).collect(Collectors.toList()), args.<Float>get(satArg)));
		}
	}
}
