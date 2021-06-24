package io.github.apace100.origins.registry.forge;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.component.DummyOriginComponent;
import io.github.apace100.origins.networking.ModPackets;
import io.github.apace100.origins.networking.packet.S2COriginSynchronizationPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.Optional;

public class ModComponentsArchitecturyImpl {
	public static OriginComponent getOriginComponent(Entity player) {
		if (player instanceof PlayerEntity)
			return player.getCapability(ORIGIN_COMPONENT_CAPABILITY).orElseGet(DummyOriginComponent::getInstance);
		return DummyOriginComponent.getInstance();
	}

	public static void syncOriginComponent(Entity player) {
		if (!(player.getEntityWorld().getChunkManager() instanceof ServerChunkManager))
			return; //Skip client side calls.
		if (player instanceof ServerPlayerEntity spe)
			S2COriginSynchronizationPacket.self(player).ifPresent(packet -> ModPackets.CHANNEL.sendToPlayer(spe, packet));
		S2COriginSynchronizationPacket.other(player).ifPresent(packet -> ModPackets.CHANNEL.sendToTracking(player, packet));
	}

	public static void syncWith(ServerPlayerEntity player, Entity provider) {
		Optional<S2COriginSynchronizationPacket> message;
		if (player == provider)
			message = S2COriginSynchronizationPacket.self(provider);
		else
			message = S2COriginSynchronizationPacket.other(provider);
		message.ifPresent(packet -> ModPackets.CHANNEL.sendToPlayer(player, packet));
	}

	public static Optional<OriginComponent> maybeGetOriginComponent(Entity player) {
		if (player instanceof PlayerEntity)
			return player.getCapability(ORIGIN_COMPONENT_CAPABILITY).resolve();
		return Optional.empty();
	}

	@CapabilityInject(OriginComponent.class)
	public static Capability<OriginComponent> ORIGIN_COMPONENT_CAPABILITY;

	public static class OriginStorage implements Capability.IStorage<OriginComponent> {
		@Override
		public Tag writeNBT(Capability<OriginComponent> capability, OriginComponent object, Direction arg) {
			return object.writeNbt(new CompoundTag());
		}

		@Override
		public void readNBT(Capability<OriginComponent> capability, OriginComponent object, Direction arg, Tag arg2) {
			object.readFromNbt((CompoundTag) arg2);
		}
	}
}
