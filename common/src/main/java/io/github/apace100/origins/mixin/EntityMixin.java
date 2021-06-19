package io.github.apace100.origins.mixin;

import io.github.apace100.origins.access.MovingEntity;
import io.github.apace100.origins.access.WaterMovingEntity;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.networking.ModPackets;
import io.github.apace100.origins.power.ActionOnLandPower;
import io.github.apace100.origins.power.EntityGlowPower;
import io.github.apace100.origins.power.InvulnerablePower;
import io.github.apace100.origins.power.PhasingPower;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.registry.ModPowers;
import io.netty.buffer.Unpooled;
import me.shedaniel.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements MovingEntity {

	@Shadow
	public World world;
	@Shadow
	public float distanceTraveled;
	@Shadow
	protected boolean onGround;
	@Unique
	private boolean wasGrounded = false;
	private boolean isMoving;
	private float distanceBefore;

	@Inject(method = "isFireImmune", at = @At("HEAD"), cancellable = true)
	private void makeFullyFireImmune(CallbackInfoReturnable<Boolean> cir) {
		if (OriginComponent.hasPower((Entity) (Object) this, ModPowers.FIRE_IMMUNITY.get())) {
			cir.setReturnValue(true);
		}
	}

	@Shadow
	public abstract double getFluidHeight(Tag<Fluid> fluid);

	@Shadow
	public abstract Vec3d getVelocity();

	@Shadow
	public abstract int getEntityId();

	@Inject(method = "isTouchingWater", at = @At("HEAD"), cancellable = true)
	private void makeEntitiesIgnoreWater(CallbackInfoReturnable<Boolean> cir) {
		if (OriginComponent.hasPower((Entity) (Object) this, ModPowers.IGNORE_WATER.get())) {
			if (this instanceof WaterMovingEntity) {
				if (((WaterMovingEntity) this).isInMovementPhase()) {
					cir.setReturnValue(false);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
	private void makeEntitiesGlow(CallbackInfoReturnable<Boolean> cir) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		Entity thisEntity = (Entity) (Object) this;
		//Bugfix: avoid entities that aren't currently in the world.
		boolean exists = world != null && world.getEntityById(this.getEntityId()) != null;
		if (player != null && player != thisEntity && thisEntity instanceof LivingEntity entity && exists && EntityGlowPower.shouldGlow(player, entity))
			cir.setReturnValue(true);
	}

	@Inject(method = "move", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", args = {"ldc=rest"}))
	private void checkWasGrounded(MovementType type, Vec3d movement, CallbackInfo ci) {
		wasGrounded = this.onGround;
	}

	@Environment(EnvType.CLIENT)
	@Inject(method = "fall", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;fallDistance:F", opcode = Opcodes.PUTFIELD, ordinal = 0))
	private void invokeActionOnSoftLand(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition, CallbackInfo ci) {
		if (!wasGrounded && (Object) this instanceof PlayerEntity) {
			ActionOnLandPower.execute((PlayerEntity) (Object) this);
			NetworkManager.sendToServer(ModPackets.PLAYER_LANDED, new PacketByteBuf(Unpooled.buffer()));
		}
	}

	@Inject(at = @At("HEAD"), method = "isInvulnerableTo", cancellable = true)
	private void makeOriginInvulnerable(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
		if ((Object) this instanceof PlayerEntity player) {
			OriginComponent component = ModComponentsArchitectury.getOriginComponent(player);
			if (!component.hasAllOrigins())
				cir.setReturnValue(true);
			if (InvulnerablePower.isInvulnerableTo(player, damageSource))
				cir.setReturnValue(true);
		}
	}

	@Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isWet()Z"))
	private boolean preventExtinguishingFromSwimming(Entity entity) {
		if (OriginComponent.hasPower(entity, ModPowers.SWIMMING.get()) && entity.isSwimming() && !(getFluidHeight(FluidTags.WATER) > 0)) {
			return false;
		}
		return entity.isWet();
	}

	@Inject(at = @At("HEAD"), method = "isInvisible", cancellable = true)
	private void phantomInvisibility(CallbackInfoReturnable<Boolean> info) {
		if (OriginComponent.hasPower((Entity) (Object) this, ModPowers.INVISIBILITY.get()))
			info.setReturnValue(true);
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;<init>(DDD)V"), method = "pushOutOfBlocks", cancellable = true)
	protected void pushOutOfBlocks(double x, double y, double z, CallbackInfo info) {
		if ((Entity) (Object) this instanceof LivingEntity livingEntity && PhasingPower.shouldPhaseThrough(livingEntity, new BlockPos(x, y, z)))
			info.cancel();
	}

	@Inject(method = "move", at = @At("HEAD"))
	private void saveDistanceTraveled(MovementType type, Vec3d movement, CallbackInfo ci) {
		this.isMoving = false;
		this.distanceBefore = this.distanceTraveled;
	}

	@Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
	private void checkIsMoving(MovementType type, Vec3d movement, CallbackInfo ci) {
		if (this.distanceTraveled > this.distanceBefore) {
			this.isMoving = true;
		}
	}

	@Override
	public boolean isMoving() {
		return isMoving;
	}
}
