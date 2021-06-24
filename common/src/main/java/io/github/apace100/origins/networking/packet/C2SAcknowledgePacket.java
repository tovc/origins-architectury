package io.github.apace100.origins.networking.packet;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.network.INetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.function.IntSupplier;

public class C2SAcknowledgePacket implements IntSupplier {
	public static C2SAcknowledgePacket decode(PacketByteBuf buf) {
		return new C2SAcknowledgePacket();
	}
	private int loginIndex;

	public C2SAcknowledgePacket() {

	}

	public void encode(PacketByteBuf buf) { }

	public void handle(INetworkHandler handler) {
		Origins.LOGGER.info("Received acknowledgment for login packet with id {}", this.loginIndex);
		handler.setHandled(true);
	}

	@Override
	public int getAsInt() {
		return this.loginIndex;
	}

	public int getLoginIndex() {
		return loginIndex;
	}

	public void setLoginIndex(int loginIndex) {
		this.loginIndex = loginIndex;
	}
}
