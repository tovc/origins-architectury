package io.github.apace100.origins.integration;

import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;

public interface OriginEventsArchitectury {
	Event<OriginDataLoadedCallback> ORIGIN_LAYERS_LOADED = EventFactory.createLoop();
	Event<OriginDataLoadedCallback> ORIGINS_LOADED = EventFactory.createLoop();
	Event<OriginDataLoadedCallback> POWER_TYPES_LOADED = EventFactory.createLoop();
}
