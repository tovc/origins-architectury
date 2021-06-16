package io.github.apace100.origins.networking.packet;

import io.github.apace100.origins.networking.INetworkHandler;
import io.github.apace100.origins.registry.OriginsDynamicRegistryManager;
import net.minecraft.network.PacketByteBuf;

import java.util.function.Consumer;
import java.util.function.IntSupplier;

public class S2CDynamicRegistriesPacket implements IntSupplier {
	public static S2CDynamicRegistriesPacket decode(PacketByteBuf buf) {
		return new S2CDynamicRegistriesPacket(OriginsDynamicRegistryManager.decode(buf));
	}

	private final OriginsDynamicRegistryManager manager;
	private int loginIndex;

	public S2CDynamicRegistriesPacket(OriginsDynamicRegistryManager manager) {
		this.manager = manager;
	}

	public int getLoginIndex() {
		return loginIndex;
	}

	public void setLoginIndex(int loginIndex) {
		this.loginIndex = loginIndex;
	}

	public void encode(PacketByteBuf buf) {
		this.manager.encode(buf);
	}

	public void handle(INetworkHandler handler) {
		handler.queue(() -> OriginsDynamicRegistryManager.setClientInstance(this.manager));
		handler.reply(new C2SAcknowledge());
		handler.setHandled(true);
	}

	@Override
	public int getAsInt() {
		return this.loginIndex;
	}
}
