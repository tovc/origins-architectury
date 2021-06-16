package io.github.apace100.origins;

import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.fabric.OriginsAPIImpl;
import me.shedaniel.architectury.event.events.LifecycleEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class OriginsEventsFabric {
	public static void register() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> OriginsAPIImpl.currentServer = server);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> OriginsAPIImpl.currentServer = null);
	}
}
