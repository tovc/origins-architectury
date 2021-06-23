package io.github.apace100.origins.mixin;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.IInventoryPower;
import io.github.apace100.origins.power.ActionOnWakeUpPower;
import io.github.apace100.origins.power.RestrictArmorPower;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.registry.ModDamageSources;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Nameable, CommandOutput {

	@Shadow
	@Final
	public PlayerInventory inventory;
	@Shadow
	protected boolean isSubmergedInWater;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow
	public abstract boolean damage(DamageSource source, float amount);

	@Shadow
	public abstract ItemStack getEquippedStack(EquipmentSlot slot);

	@Shadow
	public abstract EntityDimensions getDimensions(EntityPose pose);

	@Shadow
	public abstract ItemEntity dropItem(ItemStack stack, boolean retainOwnership);

	@Inject(method = "updateSwimming", at = @At("TAIL"))
	private void updateSwimmingPower(CallbackInfo ci) {
		if (OriginComponent.hasPower(this, ModPowers.SWIMMING.get())) {
			this.setSwimming(this.isSprinting() && !this.hasVehicle());
			this.touchingWater = this.isSwimming();
			if (this.isSwimming()) {
				this.fallDistance = 0.0F;
				Vec3d look = this.getRotationVector();
				move(MovementType.SELF, new Vec3d(look.x / 4, look.y / 4, look.z / 4));
			}
		} else if (OriginComponent.hasPower(this, ModPowers.IGNORE_WATER.get())) {
			this.setSwimming(false);
		}
	}

	@Inject(method = "wakeUp(ZZ)V", at = @At("HEAD"))
	private void invokeWakeUpAction(boolean bl, boolean updateSleepingPlayers, CallbackInfo ci) {
		if (!bl && !updateSleepingPlayers && getSleepingPosition().isPresent()) {
			BlockPos sleepingPos = getSleepingPosition().get();
			ActionOnWakeUpPower.execute((PlayerEntity) (LivingEntity) this, sleepingPos);
		}
	}

	// Prevent healing if DisableRegenPower
	// Note that this function was called "shouldHeal" instead of "canFoodHeal" at some point in time.
	@Inject(method = "canFoodHeal", at = @At("HEAD"), cancellable = true)
	private void disableHeal(CallbackInfoReturnable<Boolean> info) {
		if (OriginComponent.hasPower(this, ModPowers.DISABLE_REGEN.get()))
			info.setReturnValue(false);
	}

	// ModifyExhaustion
	@ModifyVariable(at = @At("HEAD"), method = "addExhaustion", ordinal = 0, name = "exhaustion")
	private float modifyExhaustion(float exhaustionIn) {
		return OriginComponent.modify(this, ModPowers.MODIFY_EXHAUSTION.get(), exhaustionIn);
	}

	// NO_COBWEB_SLOWDOWN
	@Inject(at = @At("HEAD"), method = "slowMovement", cancellable = true)
	public void slowMovement(BlockState state, Vec3d multiplier, CallbackInfo info) {
		if (OriginComponent.hasPower(this, ModPowers.NO_COBWEB_SLOWDOWN.get()))
			info.cancel();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Inject(method = "dropInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;dropAll()V"))
	private void dropAdditionalInventory(CallbackInfo ci) {
		PlayerEntity thisPlayer = (PlayerEntity) (LivingEntity) this;
		ModComponentsArchitectury.getOriginComponent(this).getPowers().stream().filter(x -> x.isActive(thisPlayer) && x.getFactory() instanceof IInventoryPower).forEach(x -> {
			IInventoryPower power = (IInventoryPower<?>) x.getFactory();
			if (power.shouldDropOnDeath(x, thisPlayer)) {
				Inventory inventory = power.getInventory(x, thisPlayer);
				for (int i = 0; i < this.inventory.size(); ++i) {
					ItemStack itemStack = this.inventory.getStack(i);
					if (power.shouldDropOnDeath(x, thisPlayer, itemStack)) {
						if (!itemStack.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemStack)) {
							inventory.removeStack(i);
						} else {
							thisPlayer.dropItem(itemStack, true, false);
							inventory.setStack(i, ItemStack.EMPTY);
						}
					}
				}
			}
		});
	}

	@Inject(method = "canEquip", at = @At("HEAD"), cancellable = true)
	private void preventArmorDispensing(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		EquipmentSlot slot = MobEntity.getPreferredEquipmentSlot(stack);
		OriginComponent component = ModComponentsArchitectury.getOriginComponent(this);
		//FIXME Why isn't ConditionedRestrictArmor here ?
		if (RestrictArmorPower.isForbidden((PlayerEntity) (LivingEntity) this, slot, stack))
			info.setReturnValue(false);
		if (stack.getItem() == Items.ELYTRA && OriginComponent.hasPower(this, ModPowers.ELYTRA_FLIGHT.get()))
			info.setReturnValue(false);
	}

	// WATER_BREATHING
	@Inject(at = @At("TAIL"), method = "tick")
	private void tick(CallbackInfo info) {
		if (OriginComponent.hasPower(this, ModPowers.WATER_BREATHING.get())) {
			if (!this.isSubmergedIn(FluidTags.WATER) && !this.hasStatusEffect(StatusEffects.WATER_BREATHING) && !this.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
				if (!this.isRainingAtPlayerPosition()) {
					int landGain = this.getNextAirOnLand(0);
					this.setAir(this.getNextAirUnderwater(this.getAir()) - landGain);
					if (this.getAir() == -20) {
						this.setAir(0);

						for (int i = 0; i < 8; ++i) {
							double f = this.random.nextDouble() - this.random.nextDouble();
							double g = this.random.nextDouble() - this.random.nextDouble();
							double h = this.random.nextDouble() - this.random.nextDouble();
							this.world.addParticle(ParticleTypes.BUBBLE, this.getParticleX(0.5), this.getEyeY() + this.random.nextGaussian() * 0.08D, this.getParticleZ(0.5), f * 0.5F, g * 0.5F + 0.25F, h * 0.5F);
						}

						this.damage(ModDamageSources.NO_WATER_FOR_GILLS, 2.0F);
					}
				} else {
					int landGain = this.getNextAirOnLand(0);
					this.setAir(this.getAir() - landGain);
				}
			} else if (this.getAir() < this.getMaxAir()) {
				this.setAir(this.getNextAirOnLand(this.getAir()));
			}
		}
	}

	// Copy from Entity#isBeingRainedOn
	private boolean isRainingAtPlayerPosition() {
		BlockPos blockPos = this.getBlockPos();
		return this.world.hasRain(blockPos) || this.world.hasRain(blockPos.add(0.0D, this.getDimensions(this.getPose()).height, 0.0D));
	}

	// WATER_BREATHING
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/tag/Tag;)Z"), method = "updateTurtleHelmet")
	public boolean isSubmergedInProxy(PlayerEntity player, Tag<Fluid> fluidTag) {
		boolean submerged = this.isSubmergedIn(fluidTag);
		return OriginComponent.hasPower(this, ModPowers.WATER_BREATHING.get()) != submerged;
	}
}
