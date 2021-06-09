package io.github.apace100.origins.networking.packet;

import io.github.apace100.origins.Origins;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class AcknowledgeMessage implements IntSupplier {
	private int loginIndex;

	public static AcknowledgeMessage decode(PacketByteBuf buf) {
		return new AcknowledgeMessage();
	}

	public void encode(PacketByteBuf buf) { }

	public int getLoginIndex() {
		return loginIndex;
	}

	public void setLoginIndex(int loginIndex) {
		this.loginIndex = loginIndex;
	}

	public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
		Origins.LOGGER.debug("Client successfully received a login packet.");
		contextSupplier.get().setPacketHandled(true);
	}

	@Override
	public int getAsInt() {
		return this.loginIndex;
	}
}
