package io.github.apace100.origins.networking.forge.packet;

import com.google.common.collect.ImmutableList;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.OriginsForge;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class S2CDisplayOriginSelection implements IntSupplier {
	private int loginIndex;

	public S2CDisplayOriginSelection() { }

	public S2CDisplayOriginSelection(PacketByteBuf buf) { }

	public void encode(PacketByteBuf buf) { }

	public int loginIndex() {
		return this.loginIndex;
	}

	public void setLoginIndex(int loginIndex) {
		this.loginIndex = loginIndex;
	}

	public void accept(Supplier<NetworkEvent.Context> contextSupplier) {
		OriginsForge.SHOULD_QUEUE_SCREEN = true;
		OriginsForge.channel.reply(new C2SAcknowldge(), contextSupplier.get());
		contextSupplier.get().setPacketHandled(true);
	}

	@Override
	public int getAsInt() {
		return this.loginIndex;
	}
}
