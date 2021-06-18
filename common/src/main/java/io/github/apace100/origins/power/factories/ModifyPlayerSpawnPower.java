package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.power.ModifyPlayerSpawnConfiguration;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ModifyPlayerSpawnPower extends PowerFactory<ModifyPlayerSpawnConfiguration> {

	public ModifyPlayerSpawnPower() {
		super(ModifyPlayerSpawnConfiguration.CODEC);
	}

	@Override
	public void onChosen(ConfiguredPower<ModifyPlayerSpawnConfiguration, ?> configuration, PlayerEntity player, boolean isOrbOfOrigin) {
		super.onChosen(configuration, player, isOrbOfOrigin);
	}

	@Override
	public void onRemoved(ConfiguredPower<ModifyPlayerSpawnConfiguration, ?> configuration, PlayerEntity player) {
		if (player instanceof ServerPlayerEntity serverPlayer) {
			if (serverPlayer.getSpawnPointPosition() != null && serverPlayer.isSpawnPointSet())
				serverPlayer.setSpawnPoint(World.OVERWORLD, null, 0F, false, false);
		}
	}

	@Override
	protected void onChosen(ModifyPlayerSpawnConfiguration configuration, PlayerEntity player, boolean isOrbOfOrigin) {
		if (player instanceof ServerPlayerEntity serverPlayer) {
			Pair<ServerWorld, BlockPos> spawn = configuration.getSpawn(player, false);
			if (spawn != null) {
				if (!isOrbOfOrigin) {
					Vec3d tpPos = Dismounting.method_30769(EntityType.PLAYER, spawn.getLeft(), spawn.getRight(), true);
					if (tpPos != null)
						serverPlayer.teleport(spawn.getLeft(), tpPos.x, tpPos.y, tpPos.z, player.pitch, player.yaw);
					else {
						serverPlayer.teleport(spawn.getLeft(), spawn.getRight().getX(), spawn.getRight().getY(), spawn.getRight().getZ(), player.pitch, player.yaw);
						Origins.LOGGER.warn("Could not spawn player with `ModifySpawnPower` at the desired location.");
					}
				}
			}
		}
	}
}

