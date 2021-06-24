package io.github.apace100.origins.networking.fabric;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.network.INetworkHandler;
import io.github.apace100.origins.api.network.MessageDefinition;
import io.github.apace100.origins.api.network.NetworkChannel;
import io.github.apace100.origins.networking.client.ClientLoginHandler;
import io.github.apace100.origins.networking.client.ClientPlayHandler;
import io.github.apace100.origins.networking.server.ServerLoginHandler;
import io.github.apace100.origins.networking.server.ServerPlayHandler;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.shedaniel.architectury.utils.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class NetworkChannelFabric {

	private final NetworkChannel channel;
	private final Int2ObjectMap<MessageDefinition<?>> definitions;
	private final Object2IntMap<Class<?>> messageClasses;

	public NetworkChannelFabric(NetworkChannel channel) {
		this.channel = channel;
		this.definitions = new Int2ObjectOpenHashMap<>();
		this.messageClasses = new Object2IntOpenHashMap<>();
	}

	public NetworkChannel getChannel() {
		return channel;
	}

	public void registerEndpoints() {
		EnvExecutor.runInEnv(EnvType.CLIENT, () -> () -> {
			ClientLoginNetworking.registerGlobalReceiver(channel.channel(), this::receiveLoginClient);
			ClientPlayNetworking.registerGlobalReceiver(channel.channel(), this::receivePlayClient);
		});
		ServerLoginNetworking.registerGlobalReceiver(channel.channel(), this::receiveLoginServer);
		ServerPlayNetworking.registerGlobalReceiver(channel.channel(), this::receivePlayServer);
	}

	private void receivePlayServer(MinecraftServer server, ServerPlayerEntity playerEntity, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
		byte messageId = buf.readByte();
		MessageDefinition<?> messageDefinition = this.definitions.get(messageId);
		this.handle(messageId, messageDefinition, buf, new ServerPlayHandler(this, server, playerEntity, handler::disconnect, sender));
	}

	private void receiveLoginServer(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender responseSender) {
		if (!understood) {
			Origins.LOGGER.error("[{}]Client failed to understand a login packet, disconnecting.", this.channel.channel());
			handler.disconnect(new LiteralText("This server uses " + this.channel.channel() + " with network version: " + this.channel.version().get() + " which your client does not support."));
		} else {
			byte messageId = buf.readByte();
			MessageDefinition<?> messageDefinition = this.definitions.get(messageId);
			this.handle(messageId, messageDefinition, buf, new ServerLoginHandler(this, server, handler::disconnect, responseSender));
		}
	}

	@Environment(EnvType.CLIENT)
	private CompletableFuture<PacketByteBuf> receiveLoginClient(MinecraftClient client, ClientLoginNetworkHandler handler, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder) {
		byte messageId = buf.readByte();
		MessageDefinition<?> messageDefinition = this.definitions.get(messageId);
		ClientLoginHandler networkHandler = new ClientLoginHandler(this, client, handler.getConnection());
		this.handle(messageId, messageDefinition, buf, networkHandler);
		return networkHandler.response();
	}

	@Environment(EnvType.CLIENT)
	private void receivePlayClient(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		byte messageId = buf.readByte();
		MessageDefinition<?> messageDefinition = this.definitions.get(messageId);
		this.handle(messageId, messageDefinition, buf, new ClientPlayHandler(this, client, responseSender, handler.getConnection()));
	}

	public <MSG> Packet<?> encode(PacketSender sender, MSG message) {
		return sender.createPacket(this.channel.channel(), this.encode(message));
	}

	public <MSG> PacketByteBuf encode(MSG message) {
		Pair<Integer, MessageDefinition<MSG>> definition = this.findDefinition(message);
		PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
		packetByteBuf.writeByte(definition.getLeft());
		definition.getRight().encoder().accept(message, packetByteBuf);
		return packetByteBuf;
	}

	public <MSG> void handle(byte id, MessageDefinition<MSG> definition, PacketByteBuf buf, INetworkHandler handler) {
		if (definition == null) {
			Origins.LOGGER.error("[{}]No message with id {} were registered", this.channel.channel(), id);
			return;
		}
		try {
			MSG apply = definition.decoder().apply(buf);
			definition.handler().accept(apply, handler);
		} catch (Throwable t) {
			Origins.LOGGER.error("[{}]Failed to handle message {}.", this.channel.channel(), id, t);
		}
	}

	@SuppressWarnings("unchecked")
	public <MSG> Pair<Integer, MessageDefinition<MSG>> findDefinition(MSG message) {
		Optional<Pair<Integer, MessageDefinition<MSG>>> any = messageClasses.object2IntEntrySet().stream().filter(x -> x.getKey().isInstance(message))
				.mapToInt(Object2IntMap.Entry::getIntValue)
				.mapToObj(x -> Pair.of(x, (MessageDefinition<MSG>) this.definitions.get(x)))
				.filter(x -> x.getRight() != null)
				.findAny();
		if (any.isEmpty()) {
			Origins.LOGGER.error("[{}]Tried to send a message of class {} which isn't registered.", this.channel.channel(), message.getClass().getSimpleName());
			throw new RuntimeException("Missing message type: " + message.getClass().getSimpleName());
		}
		return any.get();
	}

	@Environment(EnvType.CLIENT)
	public <MSG> void sendToServer(MSG message) {
		ClientPlayNetworking.send(this.channel.channel(), this.encode(message));
	}

	public <MSG> void sendToPlayer(ServerPlayerEntity player, MSG message) {
		ServerPlayNetworking.send(player, this.channel.channel(), this.encode(message));
	}

	public <MSG> void sendToTracking(Entity entity, MSG message) {
		((ServerChunkManager)entity.getEntityWorld().getChunkManager()).sendToOtherNearbyPlayers(entity, new CustomPayloadS2CPacket(this.channel.channel(), this.encode(message)));
	}

	public <MSG> void sendToTrackingAndSelf(Entity entity, MSG message) {
		((ServerChunkManager)entity.getEntityWorld().getChunkManager()).sendToNearbyPlayers(entity, new CustomPayloadS2CPacket(this.channel.channel(), this.encode(message)));
	}
}
