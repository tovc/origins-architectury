package io.github.apace100.origins.api.event;

import io.github.apace100.origins.api.IOriginsDynamicRegistryManager;
import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;

public interface OriginsDynamicRegistryEvent {
	Event<OriginsDynamicRegistryEvent> INITIALIZE_EVENT = EventFactory.createLoop();

	void accept(IOriginsDynamicRegistryManager registryManager);
}
