package io.github.apace100.origins.mixin.fabric;

import io.github.apace100.origins.power.ModifyDamageDealtPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Replaced by an event in forge.
 */
@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity {

	protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	@ModifyVariable(method = "onEntityHit", ordinal = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;trident(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;)Lnet/minecraft/entity/damage/DamageSource;"))
	private float modifyProjectileDamageDealt(float original, EntityHitResult entityHitResult) {
		Entity owner = this.getOwner();
		if (owner != null) {
			Entity target = entityHitResult.getEntity();
			DamageSource source = DamageSource.trident(this, owner);
			return ModifyDamageDealtPower.modifyProjectile(owner, target instanceof LivingEntity le ? le : null, source, original);
		}
		return original;
	}
}
