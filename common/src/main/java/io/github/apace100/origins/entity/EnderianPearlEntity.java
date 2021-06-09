package io.github.apace100.origins.entity;

import io.github.apace100.origins.registry.ModEntities;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public class EnderianPearlEntity extends EnderPearlEntity {
	@ExpectPlatform
	public static Optional<Pair<Vec3d, Float>> fireTeleportationEvent(ServerPlayerEntity serverPlayer, EnderianPearlEntity thisEntity) {
		throw new AssertionError();
	}

	public EnderianPearlEntity(EntityType<? extends EnderianPearlEntity> type, World world) {
		super(type, world);
	}

	public EnderianPearlEntity(World world) {
		super(ModEntities.ENDERIAN_PEARL, world);
	}

	public EnderianPearlEntity(World world, LivingEntity owner) {
		this(ModEntities.ENDERIAN_PEARL, world);
		this.updatePosition(owner.getX(), owner.getEyeY() - 0.10000000149011612D, owner.getZ());
		this.setOwner(owner);
	}

	@Environment(EnvType.CLIENT)
	public EnderianPearlEntity(World world, double x, double y, double z) {
		this(world);
		this.updatePosition(x, y, z);
	}

	protected Item getDefaultItem() {
		return Items.ENDER_PEARL;
	}

	protected void onCollision(HitResult hitResult) {
		//super.onCollision(hitResult);
		HitResult.Type type = hitResult.getType();
		if (type == HitResult.Type.ENTITY)
			this.onEntityHit((EntityHitResult) hitResult);
		else if (type == HitResult.Type.BLOCK)
			this.onBlockHit((BlockHitResult) hitResult);

		Entity entity = this.getOwner();

		for (int i = 0; i < 32; ++i) {
			this.world.addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0D, this.getZ(), this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
		}

		if (!this.world.isClient && !this.removed) {
			if (entity instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) entity;
				if (serverplayerentity.networkHandler.getConnection().isOpen() && serverplayerentity.world == this.world && !serverplayerentity.isSleeping()) {
					fireTeleportationEvent(serverplayerentity, this).ifPresent(pair -> {
						if (entity.hasVehicle()) {
							entity.stopRiding();
						}
						Vec3d target = pair.getLeft();
						entity.requestTeleport(target.x, target.y, target.z);
						entity.fallDistance = 0.0F;
						if (pair.getRight() > 0)
							entity.damage(DamageSource.FALL, pair.getRight());
					});
				}
			} else if (entity != null) {
				entity.requestTeleport(this.getX(), this.getY(), this.getZ());
				entity.fallDistance = 0.0F;
			}

			this.remove();
		}
	}

	public void tick() {
		Entity entity = this.getOwner();
		if (entity instanceof PlayerEntity && !entity.isAlive()) {
			this.remove();
		} else {
			super.tick();
		}
	}

	public Entity moveToWorld(ServerWorld destination) {
		Entity entity = this.getOwner();
		if (entity != null && entity.world.getRegistryKey() != destination.getRegistryKey()) {
			this.setOwner(null);
		}

		return super.moveToWorld(destination);
	}
}
