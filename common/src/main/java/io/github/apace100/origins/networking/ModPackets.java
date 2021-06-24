package io.github.apace100.origins.networking;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.network.NetworkChannel;
import io.github.apace100.origins.api.network.PacketDirection;
import io.github.apace100.origins.networking.packet.*;
import net.minecraft.util.Identifier;

public class ModPackets {
	public static final String NETWORK_VERSION = "NET-0.8.0";
	public static final NetworkChannel CHANNEL = NetworkChannel.create(Origins.identifier("channel"), NETWORK_VERSION::equals, NETWORK_VERSION::equals, () -> NETWORK_VERSION);

	public static final Identifier HANDSHAKE = Origins.identifier("handshake");

	public static void register() {
		int index = 0;
		CHANNEL.messageBuilder(index++, C2SAcknowledgePacket.class, PacketDirection.LOGIN_SERVERBOUND)
				.decoder(C2SAcknowledgePacket::decode).encoder(C2SAcknowledgePacket::encode)
				.handler(C2SAcknowledgePacket::handle)
				.loginIndex(C2SAcknowledgePacket::getLoginIndex, C2SAcknowledgePacket::setLoginIndex)
				.add();
		CHANNEL.messageBuilder(index++, S2CDynamicRegistriesPacket.class, PacketDirection.LOGIN_CLIENTBOUND)
				.decoder(S2CDynamicRegistriesPacket::decode).encoder(S2CDynamicRegistriesPacket::encode)
				.handler(S2CDynamicRegistriesPacket::handle)
				.loginIndex(S2CDynamicRegistriesPacket::getLoginIndex, S2CDynamicRegistriesPacket::setLoginIndex)
				.markAsLoginPacket().add();


		CHANNEL.messageBuilder(index++, S2CConfirmOriginPacket.class, PacketDirection.PLAY_CLIENTBOUND)
				.decoder(S2CConfirmOriginPacket::decode).encoder(S2CConfirmOriginPacket::encode)
				.handler(S2CConfirmOriginPacket::handle).add();
		CHANNEL.messageBuilder(index++, S2COriginSynchronizationPacket.class, PacketDirection.PLAY_CLIENTBOUND)
				.decoder(S2COriginSynchronizationPacket::decode).encoder(S2COriginSynchronizationPacket::encode)
				.handler(S2COriginSynchronizationPacket::handle).add();
		CHANNEL.messageBuilder(index++, S2COpenOriginScreenPacket.class, PacketDirection.PLAY_CLIENTBOUND)
				.decoder(S2COpenOriginScreenPacket::decode).encoder(S2COpenOriginScreenPacket::encode)
				.handler(S2COpenOriginScreenPacket::handle).add();

		CHANNEL.messageBuilder(index++, C2SUseActivePowersPacket.class, PacketDirection.PLAY_SERVERBOUND)
				.decoder(C2SUseActivePowersPacket::decode).encoder(C2SUseActivePowersPacket::encode)
				.handler(C2SUseActivePowersPacket::handle).add();
		CHANNEL.messageBuilder(index++, C2SChooseOriginPacket.class, PacketDirection.PLAY_SERVERBOUND)
				.decoder(C2SChooseOriginPacket::decode).encoder(C2SChooseOriginPacket::encode)
				.handler(C2SChooseOriginPacket::handle).add();
		CHANNEL.messageBuilder(index++, C2SPlayerLandedPacket.class, PacketDirection.PLAY_SERVERBOUND)
				.decoder(C2SPlayerLandedPacket::decode).encoder(C2SPlayerLandedPacket::encode)
				.handler(C2SPlayerLandedPacket::handle).add();
		CHANNEL.messageBuilder(index++, C2SUseActivePowersPacket.class, PacketDirection.PLAY_SERVERBOUND)
				.decoder(C2SUseActivePowersPacket::decode).encoder(C2SUseActivePowersPacket::encode)
				.handler(C2SUseActivePowersPacket::handle).add();
		Origins.LOGGER.debug("Registered {} packets", index);
	}
}
