package io.github.apace100.origins.networking.packet;

import io.github.apace100.origins.api.network.INetworkHandler;
import io.github.apace100.origins.power.ActionOnLandPower;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public record C2SPlayerLandedPacket() {
	public void encode(PacketByteBuf buffer) { }
	public static C2SPlayerLandedPacket decode(PacketByteBuf buffer) { return new C2SPlayerLandedPacket(); }
	public void handle(INetworkHandler handler) {
		PlayerEntity player = handler.getPlayer();
		handler.queue(() -> ActionOnLandPower.execute(player));
		handler.setHandled(true);
	}
}
