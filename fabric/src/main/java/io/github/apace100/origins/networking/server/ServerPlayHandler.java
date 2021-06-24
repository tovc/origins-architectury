package io.github.apace100.origins.networking.server;

import io.github.apace100.origins.api.network.INetworkHandler;
import io.github.apace100.origins.networking.fabric.NetworkChannelFabric;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.function.Consumer;

public record ServerPlayHandler(NetworkChannelFabric channel, MinecraftServer server, ServerPlayerEntity player, Consumer<Text> disconnect, PacketSender sender) implements INetworkHandler {

	@Override
	public void queue(Runnable runnable) {
		this.server().execute(runnable);
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
		return this.player();
	}

	@Override
	public void disconnect(Text reason) {
		this.disconnect().accept(reason);
	}

	@Override
	public World getWorld() {
		return this.player().getEntityWorld();
	}
}
