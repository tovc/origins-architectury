package io.github.apace100.origins.mixin.forge;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.power.ModifyPlayerSpawnPower;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

//Currently dead code, will be added if #15 is reopened.
@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

	private Pair<ServerWorld, BlockPos> origins$respawnData = null;

	@Inject(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getOverworld()Lnet/minecraft/server/world/ServerWorld;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	public void respawnPlayerHook(ServerPlayerEntity player, boolean isEnd, CallbackInfoReturnable<ServerPlayerEntity> cir, BlockPos spawnPosition, float spawnAngle, boolean isSpawnSet,
								  ServerWorld spawnPointDimension, Optional<Vec3d> respawnPos) {
		if (respawnPos.isPresent())
			return;
		this.updatePlayerSpawn(player, isSpawnSet);
	}

	@ModifyVariable(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;isDemo()Z", ordinal = 0), ordinal = 1)
	public ServerWorld updateSpawnWorld(ServerWorld world) {
		return origins$respawnData != null ? origins$respawnData.getLeft() : world;
	}

	@Inject(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	public void updateSpawnPosition(ServerPlayerEntity player, boolean isEnd, CallbackInfoReturnable<ServerPlayerEntity> cir, BlockPos spawnPosition, float spawnAngle, boolean isSpawnSet,
									ServerWorld spawnPointDimension, Optional<Vec3d> respawnPos, ServerWorld respawnWorld, ServerPlayerInteractionManager interactionManager, ServerPlayerEntity newPlayer) {
		if (this.origins$respawnData != null) {
			Vec3d pos = Vec3d.of(this.origins$respawnData.getRight()).add(0.5, 0, 0.5);
			newPlayer.refreshPositionAndAngles(pos.x, pos.y, pos.z, 0.0F, 0.0F);
		}
	}

	private void updatePlayerSpawn(ServerPlayerEntity entity, boolean spawnSet) {
		this.origins$respawnData = null;
		for (ModifyPlayerSpawnPower power : OriginComponent.getPowers(entity, ModifyPlayerSpawnPower.class)) {
			this.origins$respawnData = power.getSpawn(spawnSet);
			if (origins$respawnData != null)
				return;
		}
	}
}
