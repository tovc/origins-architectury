package io.github.apace100.origins.networking.client;

import io.github.apace100.origins.api.network.INetworkHandler;
import io.github.apace100.origins.networking.fabric.NetworkChannelFabric;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public record ClientPlayHandler(NetworkChannelFabric channel, MinecraftClient client, PacketSender sender,
								ClientConnection connection) implements INetworkHandler {

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
		this.sender().sendPacket(this.channel().encode(this.sender(), message));
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
}
