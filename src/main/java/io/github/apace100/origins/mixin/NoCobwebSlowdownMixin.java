package io.github.apace100.origins.mixin;

import io.github.apace100.origins.power.OriginsPowerTypes;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import net.minecraft.commands.CommandSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class NoCobwebSlowdownMixin extends LivingEntity implements Nameable, CommandSource {
    protected NoCobwebSlowdownMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    //Different from
    @Inject(at = @At("HEAD"), method = "makeStuckInBlock", cancellable = true)
    public void slowMovement(BlockState state, Vec3 multiplier, CallbackInfo info) {
        if (state.getBlock() instanceof WebBlock && IPowerContainer.hasPower(this, OriginsPowerTypes.NO_SLOWDOWN.get()))
            info.cancel();
    }
}
