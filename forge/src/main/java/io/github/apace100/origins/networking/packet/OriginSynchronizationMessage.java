package io.github.apace100.origins.networking.packet;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;

public class OriginSynchronizationMessage {
	@OnlyIn(Dist.CLIENT)
	private static PlayerEntity localPlayer() {
		return MinecraftClient.getInstance().player;
	}

	private static Entity find(OptionalInt entity) {
		PlayerEntity playerEntity = localPlayer();
		if (entity.isPresent())
			return playerEntity.getEntityWorld().getEntityById(entity.getAsInt());
		return playerEntity;
	}

	public static OriginSynchronizationMessage decode(PacketByteBuf buffer) {
		OptionalInt entity = OptionalInt.empty();
		if (buffer.readBoolean())
			entity = OptionalInt.of(buffer.readInt());
		CompoundTag component = buffer.readCompoundTag();
		return new OriginSynchronizationMessage(entity, component);
	}

	public static Optional<OriginSynchronizationMessage> self(Entity player) {
		return ModComponentsArchitectury.maybeGetOriginComponent(player).map(OriginSynchronizationMessage::new);
	}

	public static Optional<OriginSynchronizationMessage> other(Entity provider) {
		return ModComponentsArchitectury.maybeGetOriginComponent(provider).map(x -> new OriginSynchronizationMessage(provider.getEntityId(), x));
	}

	private final OptionalInt entity;
	private final CompoundTag component;

	public OriginSynchronizationMessage(OriginComponent component) {
		this(OptionalInt.empty(), component);
	}

	public OriginSynchronizationMessage(int entity, OriginComponent component) {
		this(OptionalInt.of(entity), component);
	}

	public OriginSynchronizationMessage(OptionalInt entity, OriginComponent component) {
		this.entity = entity;
		this.component = new CompoundTag();
		component.writeToNbt(this.component);
	}

	public OriginSynchronizationMessage(OptionalInt entity, CompoundTag component) {
		this.entity = entity;
		this.component = component;
	}

	public void encode(PacketByteBuf buffer) {
		buffer.writeBoolean(entity.isPresent());
		entity.ifPresent(buffer::writeInt);
		buffer.writeCompoundTag(this.component);
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			Entity entity = find(this.entity);
			Optional<OriginComponent> originComponent = ModComponentsArchitectury.maybeGetOriginComponent(entity);
			String name = entity != null ? entity.getEntityName() : (this.entity.isPresent() ? "EntityId[" + this.entity.getAsInt() + "]" : "LocalPlayer");
			originComponent.ifPresent(x -> {
				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				try {
					buf.writeCompoundTag(this.component);
					x.applySyncPacket(buf);
				} catch (Throwable t) {
					Origins.LOGGER.error("An error occurred while synchronizing origins for \"" + name + "\"", t);
				} finally {
					buf.release();
				}
			});
			if (!originComponent.isPresent())
				Origins.LOGGER.error("Couldn't find component for entity \"{}\"", name);
		});
		contextSupplier.get().setPacketHandled(true);
	}
}
