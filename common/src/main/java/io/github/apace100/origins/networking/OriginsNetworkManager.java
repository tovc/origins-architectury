package io.github.apace100.origins.networking;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class OriginsNetworkManager {
	public static final String ABSENT = new String("ABSENT \ud83e\udd14");
	public static final String ACCEPTVANILLA = new String("ALLOWVANILLA \ud83d\udc93\ud83d\udc93\ud83d\udc93");

	@ExpectPlatform
	public static <MSG> void registerMessage(MessageDefinition<MSG> messageDefinition) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static NetworkChannel registerChannel(NetworkChannel channel) {
		throw new AssertionError();
	}
}
