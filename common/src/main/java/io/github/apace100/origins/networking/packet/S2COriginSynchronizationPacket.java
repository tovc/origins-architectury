package io.github.apace100.origins.networking.packet;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.network.INetworkHandler;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.OptionalInt;

public class S2COriginSynchronizationPacket {

	public static S2COriginSynchronizationPacket decode(PacketByteBuf buffer) {
		OptionalInt entity = OptionalInt.empty();
		if (buffer.readBoolean())
			entity = OptionalInt.of(buffer.readInt());
		CompoundTag component = buffer.readCompoundTag();
		return new S2COriginSynchronizationPacket(entity, component);
	}

	public static Optional<S2COriginSynchronizationPacket> self(Entity player) {
		return ModComponentsArchitectury.maybeGetOriginComponent(player).map(S2COriginSynchronizationPacket::new);
	}

	public static Optional<S2COriginSynchronizationPacket> other(Entity provider) {
		return ModComponentsArchitectury.maybeGetOriginComponent(provider).map(x -> new S2COriginSynchronizationPacket(provider.getEntityId(), x));
	}

	private final OptionalInt entity;
	private final CompoundTag component;

	public S2COriginSynchronizationPacket(OriginComponent component) {
		this(OptionalInt.empty(), component);
	}

	public S2COriginSynchronizationPacket(int entity, OriginComponent component) {
		this(OptionalInt.of(entity), component);
	}

	public S2COriginSynchronizationPacket(OptionalInt entity, OriginComponent component) {
		this.entity = entity;
		this.component = new CompoundTag();
		component.writeNbt(this.component);
	}

	public S2COriginSynchronizationPacket(OptionalInt entity, CompoundTag component) {
		this.entity = entity;
		this.component = component;
	}

	public void encode(PacketByteBuf buffer) {
		buffer.writeBoolean(entity.isPresent());
		entity.ifPresent(buffer::writeInt);
		buffer.writeCompoundTag(this.component);
	}

	public void handle(INetworkHandler handler) {
		handler.queue(() -> {
			Pair<Entity, String> pair = INetworkHandler.find(handler.getWorld(), this.entity);
			Entity entity = pair.getLeft();
			String name = pair.getRight();
			Optional<OriginComponent> originComponent = ModComponentsArchitectury.maybeGetOriginComponent(entity);
			originComponent.ifPresent(x -> {
				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				try {
					buf.writeCompoundTag(this.component);
					x.applySyncPacket(buf);
				} catch (Throwable t) {
					Origins.LOGGER.error("An error occurred while synchronizing conditionedOrigins for \"" + name + "\"", t);
				} finally {
					buf.release();
				}
			});
			if (originComponent.isEmpty())
				Origins.LOGGER.error("Couldn't find component for entity \"{}\"", name);
		});
		handler.setHandled(true);
	}
}
