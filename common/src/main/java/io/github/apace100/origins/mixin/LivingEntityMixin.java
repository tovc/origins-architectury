package io.github.apace100.origins.mixin;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.power.AttributeModifyingPowerFactory;
import io.github.apace100.origins.api.power.configuration.power.FieldConfiguration;
import io.github.apace100.origins.power.*;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.registry.ModPowers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow protected abstract float getJumpVelocity();

    @Shadow public abstract float getMovementSpeed();

    @Shadow private Optional<BlockPos> climbingPos;

    @Shadow public abstract boolean isHoldingOntoLadder();

    @Shadow public abstract void setHealth(float health);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "canWalkOnFluid", at = @At("HEAD"), cancellable = true)
    private void modifyWalkableFluids(Fluid fluid, CallbackInfoReturnable<Boolean> info) {
        if(OriginComponent.getPowers(this, ModPowers.WALK_ON_FLUID.get()).stream().anyMatch(p -> fluid.isIn(p.getConfiguration().value()))) {
            info.setReturnValue(true);
            info.cancel();
        }
    }

    //TODO Move this to an event.
    @Inject(method = "damage", at = @At("RETURN"))
    private void invokeHitActions(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValue()) {
            if ((Entity) (this) instanceof PlayerEntity player) {
                SelfActionWhenHitPower.execute(player, source, amount);
                AttackerActionWhenHitPower.execute(player, source, amount);
                //SelfActionOnHit
                //TargetActionOnHit
            }
            if (source.getAttacker() instanceof PlayerEntity attacker && (Entity) (this) instanceof LivingEntity living) {
                SelfCombatActionPower.onHit(attacker, living, source, amount);
                TargetCombatActionPower.onHit(attacker, living, source, amount);
            }
        }
    }

    //TODO Move this to an event.
    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onDeath(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void invokeKillAction(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof PlayerEntity attacker && (Entity) (this) instanceof LivingEntity living) {
            SelfCombatActionPower.onKill(attacker, living, source, amount);
        }
    }

    // ModifyLavaSpeedPower
    @ModifyConstant(method = "travel", constant = {
        @Constant(doubleValue = 0.5D, ordinal = 0),
        @Constant(doubleValue = 0.5D, ordinal = 1),
        @Constant(doubleValue = 0.5D, ordinal = 2)
    })
    private double modifyLavaSpeed(double original) {
        return OriginComponent.modify(this, ModPowers.MODIFY_LAVA_SPEED.get(), original);
    }

    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isWet()Z"))
    private boolean preventExtinguishingFromSwimming(LivingEntity livingEntity) {
        if(OriginComponent.hasPower(livingEntity, ModPowers.SWIMMING.get()) && livingEntity.isSwimming() && !(getFluidHeight(FluidTags.WATER) > 0)) {
            return false;
        }
        return livingEntity.isWet();
    }

    // SetEntityGroupPower
    @Inject(at = @At("HEAD"), method = "getGroup", cancellable = true)
    public void getGroup(CallbackInfoReturnable<EntityGroup> info) {
        if((Object)this instanceof PlayerEntity) {
            OriginComponent component = ModComponentsArchitectury.getOriginComponent(this);
            List<ConfiguredPower<FieldConfiguration<EntityGroup>, EntityGroupPower>> groups = component.getPowers(ModPowers.ENTITY_GROUP.get());
            if(groups.size() > 0) {
                if(groups.size() > 1) {
                    Origins.LOGGER.warn("Player " + this.getDisplayName().toString() + " has two instances of SetEntityGroupPower.");
                }
                info.setReturnValue(groups.get(0).getConfiguration().value());
            }
        }
    }

    // HOTBLOODED
    @Inject(at = @At("HEAD"), method= "canHaveStatusEffect", cancellable = true)
    private void preventStatusEffects(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> info) {
        if (((Entity) this) instanceof PlayerEntity player && EffectImmunityPower.isImmune(player, effect))
            info.setReturnValue(false);
    }

    // CLIMBING
    @Inject(at = @At("RETURN"), method = "isClimbing", cancellable = true)
    public void doSpiderClimbing(CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue() && (Entity) this instanceof PlayerEntity player && ClimbingPower.check(player, t -> this.climbingPos = Optional.of(t)))
            info.setReturnValue(true);
    }

    // WATER_BREATHING
    @Inject(at = @At("HEAD"), method = "canBreatheInWater", cancellable = true)
    public void doWaterBreathing(CallbackInfoReturnable<Boolean> info) {
        if(PowerTypes.WATER_BREATHING.isActive(this)) {
            info.setReturnValue(true);
        }
    }

    // SWIM_SPEED
    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
    public void modifyUnderwaterMovementSpeed(LivingEntity livingEntity, float speedMultiplier, Vec3d movementInput) {
        livingEntity.updateVelocity((float) AttributeModifyingPowerFactory.apply(this, ModPowers.MODIFY_SWIM_SPEED.get(), speedMultiplier), movementInput);
    }

    @ModifyConstant(method = "swimUpward", constant = @Constant(doubleValue = 0.03999999910593033D))
    public double modifyUpwardSwimming(double original) {
        return AttributeModifyingPowerFactory.apply(this, ModPowers.MODIFY_SWIM_SPEED.get(), original);//OriginComponent.modify(this, ModifySwimSpeedPower.class, original);
    }

    @Environment(EnvType.CLIENT)
    @ModifyConstant(method = "knockDownwards", constant = @Constant(doubleValue = -0.03999999910593033D))
    public double swimDown(double original) {
        return AttributeModifyingPowerFactory.apply(this, ModPowers.MODIFY_SWIM_SPEED.get(), original);
    }

    // LIKE_WATER
    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;method_26317(DZLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d method_26317Proxy(LivingEntity entity, double d, boolean bl, Vec3d vec3d) {
        Vec3d oldReturn = entity.method_26317(d, bl, vec3d);
        if(PowerTypes.LIKE_WATER.isActive(this)) {
            if (Math.abs(vec3d.y - d / 16.0D) < 0.025D) {
                return new Vec3d(oldReturn.x, 0, oldReturn.z);
            }
        }
        return entity.method_26317(d, bl, vec3d);
    }

    @Unique
    private float cachedDamageAmount;

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;tryUseTotem(Lnet/minecraft/entity/damage/DamageSource;)Z"))
    private void cacheDamageAmount(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        this.cachedDamageAmount = amount;
    }

    @Inject(method = "tryUseTotem", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Hand;values()[Lnet/minecraft/util/Hand;"), cancellable = true)
    private void preventDeath(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (PreventDeathPower.tryPreventDeath((LivingEntity) (Entity) this, source, cachedDamageAmount))
            cir.setReturnValue(true);
    }
}
