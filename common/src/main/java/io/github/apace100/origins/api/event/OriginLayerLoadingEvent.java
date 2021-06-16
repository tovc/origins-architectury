package io.github.apace100.origins.api.event;

import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import me.shedaniel.architectury.event.Actor;
import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;
import net.minecraft.util.Identifier;

/**
 * This event is fired when an origin is loaded.<br/>
 * The origin passed to this event is the one with the
 * highest {@link Origin#loadingPriority()} if more than
 * one origin definition is found.
 */
public class OriginLayerLoadingEvent {
	public static final Event<Actor<OriginLayerLoadingEvent>> ORIGIN_LAYER_LOADING = EventFactory.createActorLoop();

	private final Identifier identifier;
	private final OriginLayer original;
	private final OriginLayer.Builder builder;

	public OriginLayerLoadingEvent(Identifier identifier, OriginLayer original) {
		this.identifier = identifier;
		this.original = original;
		this.builder = original.copyOf();
	}

	public OriginLayer getOriginal() {
		return original;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public OriginLayer.Builder getBuilder() {
		return builder;
	}
}
