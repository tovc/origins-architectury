package io.github.apace100.origins.networking.packet;

import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.IActivePower;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.network.INetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record C2SUseActivePowersPacket(Identifier[] powers) {
	public void encode(PacketByteBuf buffer) {
		buffer.writeVarInt(this.powers().length);
		for (Identifier identifier : this.powers())
			buffer.writeIdentifier(identifier);
	}

	public static C2SUseActivePowersPacket decode(PacketByteBuf buffer) {
		int count = buffer.readVarInt();
		Identifier[] identifiers = new Identifier[count];
		for (int i = 0; i < count; i++)
			identifiers[i] = buffer.readIdentifier();
		return new C2SUseActivePowersPacket(identifiers);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void handle(INetworkHandler handler) {
		PlayerEntity player = handler.getPlayer();
		handler.queue(() -> {
			OriginComponent component = OriginsAPI.getComponent(player);
			for (Identifier id : this.powers()) {
				ConfiguredPower<?, ?> power = component.getPower(id);
				if (power != null && power.getFactory() instanceof IActivePower active)
					active.activate(power, player);
			}
		});
		handler.setHandled(true);
	}
}
