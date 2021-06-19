package io.github.apace100.origins.mixin.fabric;

import io.github.apace100.origins.power.PowerTypes;
import io.github.apace100.origins.power.ModifyJumpPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	// SLOW_FALLING
	@ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"), method = "travel", name = "d", ordinal = 0)
	public double doAvianSlowFalling(double in) {
		if(PowerTypes.SLOW_FALLING.isActive(this)) {
			this.fallDistance = 0;
			if(this.getVelocity().y <= 0.0D) {
				return 0.01D;
			}
		}
		return in;
	}

	// SPRINT_JUMP
	@Inject(at = @At("HEAD"), method = "getJumpVelocity", cancellable = true)
	private void modifyJumpVelocity(CallbackInfoReturnable<Float> info) {
		float base = 0.42F * this.getJumpVelocityMultiplier();
		float modified = (float) ModifyJumpPower.apply((LivingEntity) (Entity) this, base);
		info.setReturnValue(modified);
	}
}
