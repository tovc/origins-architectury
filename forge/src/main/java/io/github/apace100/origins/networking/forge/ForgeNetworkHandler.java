package io.github.apace100.origins.networking.forge;

import io.github.apace100.origins.api.network.INetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public record ForgeNetworkHandler(SimpleChannel channel,
								  Supplier<NetworkEvent.Context> contextSupplier) implements INetworkHandler {

	@Override
	public void queue(Runnable runnable) {
		contextSupplier().get().enqueueWork(runnable);
	}

	@Override
	public void setHandled(boolean handled) {
		contextSupplier().get().setPacketHandled(handled);
	}

	@Override
	public <MSG> void reply(MSG message) {
		channel.reply(message, contextSupplier.get());
	}

	@Override
	public PlayerEntity getPlayer() {
		PlayerEntity playerEntity = DistExecutor.safeRunForDist(() -> INetworkHandler::localPlayer, () -> () -> null);
		ServerPlayerEntity sender = contextSupplier().get().getSender();
		return sender != null ? sender : playerEntity;
	}

	@Override
	public void disconnect(Text reason) {
		if (EffectiveSide.get().isServer())
			contextSupplier.get().getNetworkManager().send(new DisconnectS2CPacket(reason));
		contextSupplier.get().getNetworkManager().disconnect(reason);
	}

	@Override
	public World getWorld() {
		PlayerEntity player = this.getPlayer();
		return player != null ? player.getEntityWorld() : null;
	}
}
