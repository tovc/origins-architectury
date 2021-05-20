package io.github.apace100.origins.networking.forge.packet;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.OriginsForge;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class C2SAcknowldge implements IntSupplier {

	private int loginIndex;

	public C2SAcknowldge() {
	}

	public C2SAcknowldge(PacketByteBuf buf) {
	}

	public void encode(PacketByteBuf buf) {
	}

	public int loginIndex() {
		return this.loginIndex;
	}

	public void setLoginIndex(int loginIndex) {
		this.loginIndex = loginIndex;
	}

	public void accept(Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().setPacketHandled(true);
	}

	@Override
	public int getAsInt() {
		return this.loginIndex;
	}
}
