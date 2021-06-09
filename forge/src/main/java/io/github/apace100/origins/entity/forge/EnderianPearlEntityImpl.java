package io.github.apace100.origins.entity.forge;

import io.github.apace100.origins.entity.EnderianPearlEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public class EnderianPearlEntityImpl {
	public static Optional<Pair<Vec3d, Float>> fireTeleportationEvent(ServerPlayerEntity serverPlayer, EnderianPearlEntity thisEntity) {
		EntityTeleportEvent.EnderPearl event = ForgeEventFactory.onEnderPearlLand(serverPlayer, thisEntity.getX(), thisEntity.getY(), thisEntity.getZ(), thisEntity, 0.0F);
		return event.isCanceled() ? Optional.empty() : Optional.of(Pair.of(event.getTarget(), event.getAttackDamage()));
	}
}
