package io.github.apace100.origins.mixin.forge;

import io.github.apace100.origins.power.PowerTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	@Final
	@Shadow(remap = false)
	private static EntityAttributeModifier SLOW_FALLING;

	public LivingEntityMixin(EntityType<?> p_i48580_1_, World p_i48580_2_) {
		super(p_i48580_1_, p_i48580_2_);
	}

	@Inject(method = "travel",
			locals = LocalCapture.CAPTURE_FAILHARD,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeInstance;getValue()D", ordinal = 0))
	public void doAvianSlowFall(Vec3d p_213352_1_, CallbackInfo ci, double d0, EntityAttributeInstance gravity, boolean flag) {
		if (flag && PowerTypes.SLOW_FALLING.isActive(this)) {
			//Will be removed if neither the potion effect nor the power is active.
			//Currently these won't stack. It is technically possible to make it so they stack,
			//but that won't keep parity between both versions.
			if (!gravity.hasModifier(SLOW_FALLING))
				gravity.addTemporaryModifier(SLOW_FALLING);
			this.fallDistance = 0.0F;
		}
	}
}
