package io.github.apace100.origins.api.event;

import io.github.apace100.origins.api.power.PowerData;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import me.shedaniel.architectury.event.Actor;
import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;
import net.minecraft.util.Identifier;


/**
 * This event is fired when a power is loaded.<br/>
 * The power passed to this event is the one with the
 * highest {@link PowerData#loadingPriority()} if more than
 * one power definition is found.<br/>
 * This power currently allows you to alter
 */
public class PowerLoadingEvent {
	public static final Event<Actor<PowerLoadingEvent>> POWER_LOADING = EventFactory.createActorLoop();

	private final Identifier identifier;
	private final ConfiguredPower<?, ?> original;
	private final PowerData.Builder builder;

	public PowerLoadingEvent(Identifier identifier, ConfiguredPower<?, ?> original, PowerData data) {
		this.identifier = identifier;
		this.original = original;
		this.builder = data.copyOf();
	}

	public ConfiguredPower<?, ?> getOriginal() {
		return original;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public PowerData.Builder getBuilder() {
		return builder;
	}
}
