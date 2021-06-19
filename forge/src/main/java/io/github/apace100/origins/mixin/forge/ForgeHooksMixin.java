package io.github.apace100.origins.mixin.forge;

import io.github.apace100.origins.power.ClimbingPower;
import io.github.apace100.origins.power.ModifyHarvestPower;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeHooks.class)
public class ForgeHooksMixin {

	@Inject(method = "canHarvestBlock", remap = false, at = @At("HEAD"), cancellable = true)
	private static void canHarvestBlockHook(BlockState state, PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		ModifyHarvestPower.isHarvestAllowed(player, new CachedBlockPosition(world instanceof WorldView ? (WorldView) world : player.world, pos, true))
				.map(b -> ForgeEventFactory.doPlayerHarvestCheck(player, state, b))
				.ifPresent(cir::setReturnValue);
	}

	@Inject(method = "isLivingOnLadder", remap = false, at = @At("RETURN"), cancellable = true)
	private static void ladder(BlockState state, World world, BlockPos pos, LivingEntity entity, CallbackInfoReturnable<Boolean> info) {
		if (!info.getReturnValue()) {
			if (entity instanceof PlayerEntity player) {
				if (ClimbingPower.check(player, t -> {}))
					info.setReturnValue(true);
			}
		}
	}
}
