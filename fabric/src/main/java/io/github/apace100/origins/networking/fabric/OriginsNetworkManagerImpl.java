package io.github.apace100.origins.networking.fabric;

import io.github.apace100.origins.networking.MessageDefinition;
import io.github.apace100.origins.networking.NetworkChannel;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;

public class OriginsNetworkManagerImpl {

	public static final HashMap<Identifier, NetworkChannel> channels = new HashMap<>();

	public static <MSG> void registerMessage(MessageDefinition<MSG> definition) {

	}

	public static void initialize() {
	}

	public static NetworkChannel registerChannel(NetworkChannel channel) {
		throw new NotImplementedException("Fabric isn't my biggest concern right now.");
	}
}
