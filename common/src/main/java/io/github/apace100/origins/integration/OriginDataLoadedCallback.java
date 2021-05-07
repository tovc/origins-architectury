package io.github.apace100.origins.integration;

import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;

/**
 * Callback which is called when all of Origins data is loaded.
 * This includes powers, origins and layers.
 * It is not only called on the server, but also on the client when they
 * have received this data from the server and incorporated it into the registries.
 *
 */
public interface OriginDataLoadedCallback {

    Event<OriginDataLoadedCallback> ORIGIN_LAYERS_LOADED = EventFactory.createLoop();
    Event<OriginDataLoadedCallback> ORIGINS_LOADED = EventFactory.createLoop();
    Event<OriginDataLoadedCallback> POWER_TYPES_LOADED = EventFactory.createLoop();


    /**
     * @deprecated Use {@link #ORIGIN_LAYERS_LOADED} instead.
     */
    @Deprecated
    Event<OriginDataLoadedCallback> EVENT = ORIGIN_LAYERS_LOADED;

    void onDataLoaded(boolean isClient);
}