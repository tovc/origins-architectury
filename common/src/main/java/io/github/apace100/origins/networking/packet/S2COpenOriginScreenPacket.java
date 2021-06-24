package io.github.apace100.origins.networking.packet;

import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.network.INetworkHandler;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.screen.ChooseOriginScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.Collections;

public record S2COpenOriginScreenPacket(boolean showBackground) {
	public static S2COpenOriginScreenPacket decode(PacketByteBuf buf) {
		return new S2COpenOriginScreenPacket(buf.readBoolean());
	}

	public void encode(PacketByteBuf buf) {
		buf.writeBoolean(this.showBackground());
	}

	public void handle(INetworkHandler handler) {
		handler.queue(() -> {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			if (minecraftClient.currentScreen instanceof ChooseOriginScreen)
				return;
			ArrayList<OriginLayer> layers = new ArrayList<>();
			OriginComponent component = ModComponentsArchitectury.getOriginComponent(minecraftClient.player);
			OriginsAPI.getLayers().forEach(layer -> {
				if (layer.enabled() && !component.hasOrigin(layer))
					layers.add(layer);
			});
			Collections.sort(layers);
			minecraftClient.openScreen(new ChooseOriginScreen(layers, 0, this.showBackground()));
		});
		handler.setHandled(true);
	}
}
