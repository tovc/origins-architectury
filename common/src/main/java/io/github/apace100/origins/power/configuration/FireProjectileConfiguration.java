package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.power.IActivePower;
import io.github.apace100.origins.api.power.configuration.power.IActiveCooldownPowerConfiguration;
import io.github.apace100.origins.util.HudRender;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record FireProjectileConfiguration(int duration, HudRender hudRender, EntityType<?> entityType,
										  int projectileCount, float speed, float divergence,
										  @Nullable SoundEvent soundEvent, @Nullable CompoundTag tag,
										  IActivePower.Key key) implements IActiveCooldownPowerConfiguration {

	public static final Codec<FireProjectileConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("cooldown").forGetter(FireProjectileConfiguration::duration),
			HudRender.CODEC.fieldOf("hud_render").forGetter(FireProjectileConfiguration::hudRender),
			Registry.ENTITY_TYPE.fieldOf("entity_type").forGetter(FireProjectileConfiguration::entityType),
			Codec.INT.optionalFieldOf("count", 1).forGetter(FireProjectileConfiguration::projectileCount),
			Codec.FLOAT.optionalFieldOf("speed", 1.5F).forGetter(FireProjectileConfiguration::speed),
			Codec.FLOAT.optionalFieldOf("divergence", 1.0F).forGetter(FireProjectileConfiguration::divergence),
			OriginsCodecs.OPTIONAL_SOUND_EVENT.optionalFieldOf("sound", Optional.empty()).forGetter(x -> Optional.ofNullable(x.soundEvent())),
			OriginsCodecs.NBT.optionalFieldOf("tag").forGetter(x -> Optional.ofNullable(x.tag())),
			IActivePower.Key.BACKWARD_COMPATIBLE_CODEC.optionalFieldOf("key", IActivePower.Key.PRIMARY).forGetter(FireProjectileConfiguration::key)
	).apply(instance, (t1, t2, t3, t4, t5, t6, t7, t8, t9) -> new FireProjectileConfiguration(t1, t2, t3, t4, t5, t6, t7.orElse(null), t8.orElse(null), t9)));


	public void fireProjectiles(PlayerEntity player) {
		if (soundEvent != null) {
			player.world.playSound(null, player.getX(), player.getY(), player.getZ(), soundEvent, SoundCategory.NEUTRAL, 0.5F, 0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F));
		}
		if (!player.world.isClient) {
			for (int i = 0; i < projectileCount; i++) {
				fireProjectile(player);
			}
		}
	}

	private void fireProjectile(PlayerEntity player) {
		if (entityType != null) {
			Entity entity = entityType.create(player.world);
			if (entity == null) {
				return;
			}
			Vec3d rotationVector = player.getRotationVector();
			Vec3d spawnPos = player.getPos().add(0, player.getEyeHeight(player.getPose(), player.getDimensions(player.getPose())), 0).add(rotationVector);
			entity.refreshPositionAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), player.pitch, player.yaw);
			if (entity instanceof ProjectileEntity projectile) {
				if (projectile instanceof ExplosiveProjectileEntity explosiveProjectileEntity) {
					explosiveProjectileEntity.posX = rotationVector.x * speed;
					explosiveProjectileEntity.posY = rotationVector.y * speed;
					explosiveProjectileEntity.posZ = rotationVector.z * speed;
				}
				projectile.setOwner(player);
				projectile.setProperties(player, player.pitch, player.yaw, 0F, speed, divergence);
			} else {
				float f = -MathHelper.sin(player.yaw * 0.017453292F) * MathHelper.cos(player.pitch * 0.017453292F);
				float g = -MathHelper.sin(player.pitch * 0.017453292F);
				float h = MathHelper.cos(player.yaw * 0.017453292F) * MathHelper.cos(player.pitch * 0.017453292F);
				Vec3d vec3d = (new Vec3d(f, g, h)).normalize().add(player.getRandom().nextGaussian() * 0.007499999832361937D * (double) divergence, player.getRandom().nextGaussian() * 0.007499999832361937D * (double) divergence, player.getRandom().nextGaussian() * 0.007499999832361937D * (double) divergence).multiply(speed);
				entity.setVelocity(vec3d);
				Vec3d playerVelo = player.getVelocity();
				entity.setVelocity(entity.getVelocity().add(playerVelo.x, player.isOnGround() ? 0.0D : playerVelo.y, playerVelo.z));
			}
			if (tag != null) {
				CompoundTag mergedTag = entity.toTag(new CompoundTag());
				mergedTag.copyFrom(tag);
				entity.fromTag(mergedTag);
			}
			player.world.spawnEntity(entity);
		}
	}
}
