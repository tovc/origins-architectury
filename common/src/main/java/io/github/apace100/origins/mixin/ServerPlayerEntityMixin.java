package io.github.apace100.origins.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import io.github.apace100.origins.access.EndRespawningEntity;
import io.github.apace100.origins.power.factories.PreventSleepPower;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ScreenHandlerListener, EndRespawningEntity {

	@Shadow
	@Final
	public MinecraftServer server;
	@Shadow
	public ServerPlayNetworkHandler networkHandler;
	@Shadow
	public boolean notInAnyWorld;
	@Shadow
	private RegistryKey<World> spawnPointDimension;
	@Shadow
	private BlockPos spawnPointPosition;
	@Shadow
	private boolean spawnPointSet;
	@Unique
	private boolean origins_isEndRespawning;

	public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Shadow
	public abstract void sendMessage(Text message, boolean actionBar);

	// FRESH_AIR
	@Inject(method = "trySleep", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSpawnPoint(Lnet/minecraft/util/registry/RegistryKey;Lnet/minecraft/util/math/BlockPos;FZZ)V"), cancellable = true)
	public void preventAvianSleep(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> info) {
		if (PreventSleepPower.tryPreventSleep(this, world, pos))
			info.setReturnValue(Either.left(null));
	}

	private boolean hasObstructedSpawn() {
		ServerWorld world = server.getWorld(spawnPointDimension);
		if (spawnPointPosition != null && world != null)
			return !PlayerEntity.findRespawnPosition(world, spawnPointPosition, 0F, spawnPointSet, true).isPresent();
		return false;
	}

	@Override
	public void setEndRespawning(boolean endSpawn) {
		this.origins_isEndRespawning = endSpawn;
	}

	@Override
	public boolean isEndRespawning() {
		return this.origins_isEndRespawning;
	}

	//Kept to avoid backward compatibility issues.
	@Override
	public boolean hasRealRespawnPoint() {
		return spawnPointPosition != null && !hasObstructedSpawn();
	}
}
