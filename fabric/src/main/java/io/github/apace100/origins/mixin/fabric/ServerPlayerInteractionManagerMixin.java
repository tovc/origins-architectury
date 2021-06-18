package io.github.apace100.origins.mixin.fabric;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.power.factories.ActionOnBlockBreakPower;
import io.github.apace100.origins.power.factories.ModifyHarvestPower;
import io.github.apace100.origins.registry.ModPowers;
import io.github.apace100.origins.util.SavedBlockPosition;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {

    @Shadow public ServerWorld world;
    @Shadow public ServerPlayerEntity player;
    private SavedBlockPosition savedBlockPosition;

    @Inject(method = "tryBreakBlock", at = @At("HEAD"))
    private void cacheBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        this.savedBlockPosition = new SavedBlockPosition(world, pos);
    }

    @ModifyVariable(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;postMine(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;)V"), ordinal = 1)
    private boolean modifyEffectiveTool(boolean original) {
        return ModifyHarvestPower.isHarvestAllowed(player, savedBlockPosition).orElse(original);
    }

    @Inject(method = "tryBreakBlock", at = @At(value = "RETURN", ordinal = 4), locals = LocalCapture.CAPTURE_FAILHARD)
    private void actionOnBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState blockState, BlockEntity blockEntity, Block block, boolean bl, ItemStack itemStack, ItemStack itemStack2, boolean bl2) {
        ActionOnBlockBreakPower.execute(this.player, this.savedBlockPosition, bl && bl2);
    }
}
