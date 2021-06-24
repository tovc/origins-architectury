package io.github.apace100.origins.networking.packet;

import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.network.INetworkHandler;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.networking.ModPackets;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.screen.WaitForNextLayerScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public record S2CConfirmOriginPacket(Identifier layer, Identifier origin) {
	public static void forPlayer(ServerPlayerEntity player, Identifier layer, Identifier origin) {
		ModPackets.CHANNEL.sendToPlayer(player, new S2CConfirmOriginPacket(layer, origin));
	}

	public static S2CConfirmOriginPacket decode(PacketByteBuf buf) {
		return new S2CConfirmOriginPacket(buf.readIdentifier(), buf.readIdentifier());
	}

	public void encode(PacketByteBuf buf) {
		buf.writeIdentifier(this.layer());
		buf.writeIdentifier(this.origin());
	}

	public void handle(INetworkHandler handler) {
		OriginLayer layer = OriginsAPI.getLayers().get(this.layer());
		Origin origin = OriginsAPI.getOrigins().get(this.origin());
		handler.queue(() -> {
			OriginComponent component = ModComponentsArchitectury.getOriginComponent(handler.getPlayer());
			component.setOrigin(layer, origin);
			if (MinecraftClient.getInstance().currentScreen instanceof WaitForNextLayerScreen screen)
				screen.openSelection();
		});
		handler.setHandled(true);
	}
}
