package io.github.apace100.origins.integration;


import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Util;

import java.util.Arrays;

public interface OriginDataLoadedCallback {
	/**
	 * @deprecated Use {@link OriginEventsArchitectury#ORIGIN_LAYERS_LOADED} instead.
	 */
	@Deprecated
	Event<OriginDataLoadedCallback> EVENT = Util.make(() -> { //This is a hack on top of a hack.
		Event<OriginDataLoadedCallback> event = EventFactory.createArrayBacked(OriginDataLoadedCallback.class, arr -> isClient -> Arrays.stream(arr).forEach(x -> x.onDataLoaded(isClient)));
		OriginEventsArchitectury.ORIGIN_LAYERS_LOADED.register(event.invoker());
		return event;
	});

	void onDataLoaded(boolean isClient);
}
