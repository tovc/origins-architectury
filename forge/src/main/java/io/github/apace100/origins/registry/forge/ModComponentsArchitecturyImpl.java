package io.github.apace100.origins.registry.forge;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.OriginsForge;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.components.DummyOriginComponent;
import io.github.apace100.origins.networking.packet.OriginSynchronizationMessage;
import io.netty.buffer.Unpooled;
import me.shedaniel.architectury.networking.NetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

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
		if (player instanceof ServerPlayerEntity)
			OriginSynchronizationMessage.self(player).map(x -> OriginsForge.channel.toVanillaPacket(x, NetworkDirection.PLAY_TO_CLIENT)).ifPresent(packet -> PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player));
		OriginSynchronizationMessage.other(player).map(x -> OriginsForge.channel.toVanillaPacket(x, NetworkDirection.PLAY_TO_CLIENT)).ifPresent(packet -> {
			if (player instanceof ServerPlayerEntity)
				PacketDistributor.TRACKING_ENTITY.with(() -> player).send(packet);
			else
				PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player).send(packet);
		});
	}

	public static void syncWith(ServerPlayerEntity player, Entity provider) {
		Optional<OriginSynchronizationMessage> message;
		if (player == provider)
			message = OriginSynchronizationMessage.self(provider);
		else
			message = OriginSynchronizationMessage.other(provider);

		message.map(x -> OriginsForge.channel.toVanillaPacket(x, NetworkDirection.PLAY_TO_CLIENT))
				.ifPresent(packet -> PacketDistributor.PLAYER.with(() -> player).send(packet));
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
			CompoundTag tag = new CompoundTag();
			object.writeToNbt(tag);
			return tag;
		}

		@Override
		public void readNBT(Capability<OriginComponent> capability, OriginComponent object, Direction arg, Tag arg2) {
			object.readFromNbt((CompoundTag) arg2);
		}
	}
}
