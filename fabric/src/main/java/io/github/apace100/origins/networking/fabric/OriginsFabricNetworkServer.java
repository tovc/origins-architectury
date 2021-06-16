package io.github.apace100.origins.networking.fabric;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.networking.ModPackets;
import io.github.apace100.origins.networking.NetworkChannel;
import io.github.apace100.origins.networking.OriginsNetworkManager;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OriginsFabricNetworkServer {
	public static void register() {
		ServerLoginNetworking.registerGlobalReceiver(ModPackets.HANDSHAKE, OriginsFabricNetworkServer::handleHandshakeReply);
	}

	private static void handleHandshakeReply(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer synchronizer, PacketSender responseSender) {
		if (!understood) {
			handler.disconnect(new LiteralText("This server requires you to install the Origins mod (v" + Origins.VERSION + ") to play."));
			return;
		}
		int channels = buf.readVarInt();
		List<Pair<Identifier, String>> mismatching = new ArrayList<>();
		Set<Identifier> received = new HashSet<>();
		for (int i = 0; i < channels; i++) {
			Identifier name = buf.readIdentifier();
			NetworkChannel channel = OriginsNetworkManagerImpl.channels.get(name);
			String version = buf.readString(Short.MAX_VALUE);
			received.add(name);
			if (channel == null)
				continue; //If the channel was required serverside, the client would have disconnected itself.
			if (!channel.acceptedServerVersion().test(version))
				mismatching.add(Pair.of(name, version));
		}
		List<NetworkChannel> networkChannels = OriginsNetworkManagerImpl.channels.values().stream().filter(x -> !received.contains(x.channel())).toList();
		for (NetworkChannel networkChannel : networkChannels) {
			if (!networkChannel.acceptedServerVersion().test(OriginsNetworkManager.ABSENT))
				mismatching.add(Pair.of(networkChannel.channel(), "missing"));
		}
		if (mismatching.size() > 0)
			handler.disconnect(new TranslatableText("origins.gui.login.mismatching_channels", String.join(", ", mismatching.stream().map(x -> "[" + x.getLeft() + ": " + x.getRight() + "]").toList())));
	}
}
