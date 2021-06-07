package io.github.apace100.origins.networking.forge;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.apace100.origins.OriginsForge;
import io.github.apace100.origins.networking.packet.AcknowledgeMessage;
import io.github.apace100.origins.networking.packet.DynamicRegistryMessage;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.power.PowerTypeRegistry;
import net.minecraftforge.fml.network.FMLHandshakeHandler;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ForgeNetworkHandler {
	public static void initializeNetwork() {
		SimpleChannel channel = OriginsForge.channel;
		channel.messageBuilder(AcknowledgeMessage.class, 0, NetworkDirection.LOGIN_TO_SERVER)
				.loginIndex(AcknowledgeMessage::getLoginIndex, AcknowledgeMessage::setLoginIndex)
				.encoder(AcknowledgeMessage::encode)
				.decoder(AcknowledgeMessage::decode)
				.consumer(FMLHandshakeHandler.indexFirst((handler, message, context) -> message.handle(context)))
				.add();

		channel.messageBuilder(DynamicRegistryMessage.class, 1, NetworkDirection.LOGIN_TO_CLIENT)
				.loginIndex(DynamicRegistryMessage::getLoginIndex, DynamicRegistryMessage::setLoginIndex)
				.encoder(DynamicRegistryMessage::encode)
				.decoder(DynamicRegistryMessage::decode)
				.consumer(DynamicRegistryMessage::handle)
				.buildLoginPacketList(ForgeNetworkHandler::createRegistries)
				.add();
	}

	private static List<Pair<String, DynamicRegistryMessage>> createRegistries(boolean isLocal) {
		DynamicRegistryMessage message = new DynamicRegistryMessage(
				ImmutableMap.copyOf(PowerTypeRegistry.entries()),
				ImmutableMap.copyOf(OriginRegistry.identifiers()
						.filter(x -> !Objects.equals(x, Origin.EMPTY.getIdentifier()))
						.collect(ImmutableMap.toImmutableMap(Function.identity(), x -> OriginRegistry.get(x).dataInstance()))),
				ImmutableList.copyOf(OriginLayers.getLayers())
		);
		return ImmutableList.of(Pair.of("Origins registries", message));
	}
}
