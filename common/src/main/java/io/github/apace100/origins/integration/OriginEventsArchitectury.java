package io.github.apace100.origins.integration;

import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.power.PowerType;
import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;

public interface OriginEventsArchitectury {
	Event<OriginDataLoadedCallback> ORIGIN_LAYERS_LOADED = EventFactory.createLoop();
	Event<OriginDataLoadedCallback> ORIGINS_LOADED = EventFactory.createLoop();
	Event<OriginDataLoadedCallback> POWER_TYPES_LOADED = EventFactory.createLoop();

	Event<OriginLoadingEvent<Origin>> ORIGIN_LOADING = EventFactory.createLoop();
	Event<OriginLoadingEvent<OriginLayer>> ORIGIN_LAYER_LOADING = EventFactory.createLoop();
	Event<OriginLoadingEvent<PowerType<?>>> POWER_TYPE_LOADING = EventFactory.createLoop();
}
