package io.github.apace100.origins.networking.forge;

import io.github.apace100.origins.OriginsForge;
import io.github.apace100.origins.networking.forge.packet.C2SAcknowldge;
import io.github.apace100.origins.networking.forge.packet.S2CDisplayOriginSelection;
import net.minecraftforge.fml.network.NetworkDirection;

public class ModLoginNetworking {
	public static void init() {
		int id = 0;
		//Pipeline: ROS -> QOS
		OriginsForge.channel.messageBuilder(S2CDisplayOriginSelection.class, id++, NetworkDirection.LOGIN_TO_CLIENT)
				.loginIndex(S2CDisplayOriginSelection::loginIndex, S2CDisplayOriginSelection::setLoginIndex)
				.consumer(S2CDisplayOriginSelection::accept).markAsLoginPacket()
				.decoder(S2CDisplayOriginSelection::new).encoder(S2CDisplayOriginSelection::encode).add();
		OriginsForge.channel.messageBuilder(C2SAcknowldge.class, id++, NetworkDirection.LOGIN_TO_SERVER)
				.loginIndex(C2SAcknowldge::loginIndex, C2SAcknowldge::setLoginIndex)
				.consumer(C2SAcknowldge::accept)
				.decoder(C2SAcknowldge::new).encoder(C2SAcknowldge::encode).add();
	}
}
