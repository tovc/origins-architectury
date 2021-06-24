package io.github.apace100.origins.networking.forge;

import io.github.apace100.origins.api.network.MessageDefinition;
import io.github.apace100.origins.api.network.NetworkChannel;
import io.github.apace100.origins.api.network.PacketDirection;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class OriginsNetworkManagerImpl {
	private static final Map<Identifier, SimpleChannel> channels = new HashMap<>();

	private static NetworkDirection get(PacketDirection direction) {
		return switch (direction) {
			case LOGIN_SERVERBOUND -> NetworkDirection.LOGIN_TO_SERVER;
			case LOGIN_CLIENTBOUND -> NetworkDirection.LOGIN_TO_CLIENT;
			case PLAY_SERVERBOUND -> NetworkDirection.PLAY_TO_SERVER;
			case PLAY_CLIENTBOUND -> NetworkDirection.PLAY_TO_CLIENT;
		};
	}

	public static <MSG> void registerMessage(MessageDefinition<MSG> messageDefinition) {
		Identifier channel = messageDefinition.channel().channel();
		SimpleChannel simpleChannel = channels.get(channel);
		SimpleChannel.MessageBuilder<MSG> msgMessageBuilder = simpleChannel.messageBuilder(messageDefinition.message(), messageDefinition.index(), get(messageDefinition.direction()))
				.encoder(messageDefinition.encoder()).decoder(messageDefinition.decoder())
				.consumer((BiConsumer<MSG, Supplier<NetworkEvent.Context>>) (msg, contextSupplier) -> messageDefinition.handler().accept(msg, new ForgeNetworkHandler(simpleChannel, contextSupplier)))
				.loginIndex(messageDefinition.loginGetter() != null ? messageDefinition.loginGetter()::applyAsInt : null, messageDefinition.loginSetter() != null ? messageDefinition.loginSetter()::accept : null)
				.buildLoginPacketList(messageDefinition.loginMessages());
	}

	public static NetworkChannel registerChannel(NetworkChannel channel) {
		SimpleChannel simpleChannel = NetworkRegistry.ChannelBuilder.named(channel.channel()).networkProtocolVersion(channel.version())
				.clientAcceptedVersions(channel.acceptedClientVersion())
				.serverAcceptedVersions(channel.acceptedServerVersion())
				.simpleChannel();
		channels.put(channel.channel(), simpleChannel);
		return channel;
	}

	public static <MSG> void sendToServer(NetworkChannel channel, MSG message) {
		channels.get(channel.channel()).send(PacketDistributor.SERVER.noArg(), message);
	}

	public static <MSG> void sendToPlayer(NetworkChannel channel, ServerPlayerEntity player, MSG message) {
		channels.get(channel.channel()).send(PacketDistributor.PLAYER.with(() -> player), message);
	}

	public static <MSG> void sendToTracking(NetworkChannel channel, Entity player, MSG message) {
		channels.get(channel.channel()).send(PacketDistributor.TRACKING_ENTITY.with(() -> player), message);
	}

	public static <MSG> void sendToTrackingAndSelf(NetworkChannel channel, ServerPlayerEntity player, MSG message) {
		channels.get(channel.channel()).send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), message);
	}
}
