package io.github.apace100.origins.api.event;

import io.github.apace100.origins.api.origin.Origin;
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
public class OriginLoadingEvent {
	public static final Event<Actor<OriginLoadingEvent>> ORIGIN_LOADING = EventFactory.createActorLoop();

	private final Identifier identifier;
	private final Origin original;
	private final Origin.Builder builder;

	public OriginLoadingEvent(Identifier identifier, Origin original) {
		this.identifier = identifier;
		this.original = original;
		this.builder = original.copyOf();
	}

	public Origin getOriginal() {
		return original;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public Origin.Builder getBuilder() {
		return builder;
	}
}
