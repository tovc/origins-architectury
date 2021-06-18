package io.github.apace100.origins.mixin.fabric;

import io.github.apace100.origins.access.EntityShapeContextAccessor;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import io.github.apace100.origins.power.factories.PreventBlockActionPower;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {

    @Inject(at = @At("RETURN"), method = "calcBlockBreakingDelta", cancellable = true)
    private void modifyBlockBreakSpeed(BlockState state, PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> info) {
        //Handled via event in forge
        float base = info.getReturnValue();
        CachedBlockPosition cpb = new CachedBlockPosition(player.getEntityWorld(), pos, true);
        float modified = OriginComponent.modify(player, ModPowers.MODIFY_BREAK_SPEED.get(), base, p -> ConfiguredBlockCondition.check(p.getConfiguration().condition(), cpb));
        info.setReturnValue(modified);
    }

    //Any non full-cube shape is excluded by hooking into AbstractBlock.
    //Forge: AbstractBlockStateMixin#modifyBlockOutline
    @Inject(at = @At("RETURN"), method = "getOutlineShape", cancellable = true)
    private void modifyBlockOutline(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (context instanceof EntityShapeContext ctx) {
            Entity entity = EntityShapeContextAccessor.getEntity(ctx);
            if (entity != null && PreventBlockActionPower.isSelectionPrevented(entity, pos))
                cir.setReturnValue(VoxelShapes.empty());
        }
    }
}
