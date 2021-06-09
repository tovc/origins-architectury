package io.github.apace100.origins.entity.fabric;

import io.github.apace100.origins.entity.EnderianPearlEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public class EnderianPearlEntityImpl {
	public static Optional<Pair<Vec3d, Float>> fireTeleportationEvent(ServerPlayerEntity serverPlayer, EnderianPearlEntity thisEntity) {
		return Optional.of(Pair.of(thisEntity.getPos(), 0.0F));
	}
}
