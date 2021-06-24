package io.github.apace100.origins.networking.fabric;

import io.github.apace100.origins.api.network.MessageDefinition;
import io.github.apace100.origins.api.network.NetworkChannel;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;

public class OriginsNetworkManagerImpl {

	public static final HashMap<Identifier, NetworkChannelFabric> channels = new HashMap<>();

	public static <MSG> void registerMessage(MessageDefinition<MSG> definition) {

	}

	public static void initialize() {
	}

	public static NetworkChannel registerChannel(NetworkChannel channel) {
		throw new NotImplementedException("Fabric isn't my biggest concern right now.");
	}

	public static <MSG> void sendToServer(NetworkChannel channel, MSG message) {
		channels.get(channel.channel()).sendToServer(message);
	}

	public static <MSG> void sendToPlayer(NetworkChannel channel, ServerPlayerEntity player, MSG message) {
		channels.get(channel.channel()).sendToPlayer(player, message);
	}

	public static <MSG> void sendToTracking(NetworkChannel channel, Entity player, MSG message) {
		channels.get(channel.channel()).sendToTracking(player, message);
	}

	public static <MSG> void sendToTrackingAndSelf(NetworkChannel channel, ServerPlayerEntity player, MSG message) {
		channels.get(channel.channel()).sendToTrackingAndSelf(player, message);
	}
}
