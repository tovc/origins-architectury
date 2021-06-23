package io.github.apace100.origins.integration;

import me.shedaniel.architectury.event.Event;

/**
 * Callback which is called when all of Origins data is loaded.
 * This includes powers, conditionedOrigins and layers.
 * It is not only called on the server, but also on the client when they
 * have received this data from the server and incorporated it into the registries.
 */
public interface OriginDataLoadedCallback {
	/**
	 * @deprecated Use {@link OriginEventsArchitectury#ORIGIN_LAYERS_LOADED} instead.
	 */
	@Deprecated
	Event<OriginDataLoadedCallback> EVENT = OriginEventsArchitectury.ORIGIN_LAYERS_LOADED;

	void onDataLoaded(boolean isClient);
}