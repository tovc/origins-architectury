package io.github.apace100.origins.networking.client;

import io.github.apace100.origins.api.network.INetworkHandler;
import io.github.apace100.origins.networking.fabric.NetworkChannelFabric;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ClientLoginHandler implements INetworkHandler {
	private final NetworkChannelFabric channel;
	private final MinecraftClient client;
	private final ClientConnection connection;
	private CompletableFuture<PacketByteBuf> response;

	public ClientLoginHandler(NetworkChannelFabric channel, MinecraftClient client, ClientConnection connection) {
		this.channel = channel;
		this.client = client;
		this.connection = connection;
		this.response = null;
	}

	@Override
	public void queue(Runnable runnable) {
		this.client().execute(runnable);
	}

	@Override
	public void setHandled(boolean handled) {
		//Nothing
	}

	@Override
	public <MSG> void reply(MSG message) {
		this.response = CompletableFuture.supplyAsync(() -> this.channel().encode(message));
	}

	@Override
	public PlayerEntity getPlayer() {
		return this.client().player;
	}

	@Override
	public void disconnect(Text reason) {
		this.connection().disconnect(reason);
	}

	@Override
	public World getWorld() {
		return this.client().world;
	}

	public NetworkChannelFabric channel() { return channel; }

	public MinecraftClient client() { return client; }

	public ClientConnection connection() { return connection; }

	public CompletableFuture<PacketByteBuf> response() { return response; }

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (ClientLoginHandler) obj;
		return Objects.equals(this.channel, that.channel) &&
			   Objects.equals(this.client, that.client) &&
			   Objects.equals(this.connection, that.connection);
	}

	@Override
	public int hashCode() {
		return Objects.hash(channel, client, connection);
	}

	@Override
	public String toString() {
		return "ClientLoginNetworkHandler[" +
			   "channel=" + channel + ", " +
			   "client=" + client + ", " +
			   "connection=" + connection + ']';
	}
}
