package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.power.*;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.util.HudRender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.Tag;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ConfiguredPower<C extends IOriginsFeatureConfiguration, F extends PowerFactory<C>> extends ConfiguredFactory<C, F> {
	public static final Codec<ConfiguredPower<?, ?>> CODEC = PowerFactory.CODEC.dispatch(ConfiguredPower::getFactory, Function.identity());
	private final PowerData data;

	public ConfiguredPower(F factory, C configuration, PowerData data) {
		super(factory, configuration);
		this.data = data;
	}

	public PowerData getData() {
		return data;
	}

	public Map<String, ConfiguredPower<?, ?>> getContainedPowers() {
		return this.getFactory().getContainedPowers(this);
	}

	public boolean isActive(PlayerEntity player) {
		return this.getConfiguration().isConfigurationValid() && this.getFactory().isActive(this, player);
	}

	public void onChosen(PlayerEntity player, boolean isOrbOfOrigin) {
		this.getFactory().onChosen(this, player, isOrbOfOrigin);
	}

	public void onRemoved(PlayerEntity player) {
		this.getFactory().onRemoved(this, player);
	}

	public void onLost(PlayerEntity player) {
		this.getFactory().onLost(this, player);
	}

	public void onAdded(PlayerEntity player) {
		this.getFactory().onAdded(this, player);
	}

	public void onRespawn(PlayerEntity player) {
		this.getFactory().onRespawn(this, player);
	}

	public <C> C getPowerData(PlayerEntity player, Supplier<? extends C> supplier) {
		return OriginsAPI.getComponent(player).getPowerData(this, supplier);
	}


	public Tag serialize(PlayerEntity player) {
		return this.getFactory().serialize(this, player);
	}

	public void deserialize(PlayerEntity player, Tag tag) {
		this.getFactory().deserialize(this, player, tag);
	}

	/**
	 * Executes a tick of the current factory if it is eligible to.<br/>
	 * You cannot force a tick of a non-ticking power.
	 *
	 * @param player The player to execute the action on.
	 * @param force  If true, there won't be any check to {@link PowerFactory#tickInterval(ConfiguredPower, PlayerEntity)}.
	 *
	 * @see #tick(PlayerEntity) for a version without the ability to be forced.
	 */
	public void tick(PlayerEntity player, boolean force) {
		if (this.getFactory().canTick(this, player)) {
			if (!force) {
				int i = this.getFactory().tickInterval(this, player);
				if (i <= 0 || (player.age % i) != 0)
					return;
			}
			this.getFactory().tick(this, player);
		}
	}

	public void tick(PlayerEntity player) {
		this.tick(player, false);
	}

	//VariableIntPower
	@SuppressWarnings("unchecked")
	public Optional<IVariableIntPower<C>> asVariableIntPower() {
		if (this.getFactory() instanceof IVariableIntPower<?> variableIntPower)
			return Optional.of((IVariableIntPower<C>) variableIntPower);
		return Optional.empty();
	}

	public OptionalInt assign(PlayerEntity player, int value) {
		return this.asVariableIntPower().map(t -> t.assign(this, player, value)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	public OptionalInt getValue(PlayerEntity player) {
		return this.asVariableIntPower().map(t -> t.getValue(this, player)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	public OptionalInt getMaximum(PlayerEntity player) {
		return this.asVariableIntPower().map(t -> t.getMaximum(this, player)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	public OptionalInt getMinimum(PlayerEntity player) {
		return this.asVariableIntPower().map(t -> t.getMinimum(this, player)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	public OptionalInt change(PlayerEntity player, int amount) {
		return this.asVariableIntPower().map(t -> t.change(this, player, amount)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	public OptionalInt increment(PlayerEntity player) {
		return this.asVariableIntPower().map(t -> t.increment(this, player)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	public OptionalInt decrement(PlayerEntity player) {
		return this.asVariableIntPower().map(t -> t.decrement(this, player)).map(OptionalInt::of).orElseGet(OptionalInt::empty);
	}

	//Hud Renderered Power

	@SuppressWarnings("unchecked")
	public Optional<IHudRenderedPower<C>> asHudRendered() {
		return this.getFactory() instanceof IHudRenderedPower<?> hudRenderedPower ? Optional.of((IHudRenderedPower<C>) hudRenderedPower) : Optional.empty();
	}

	public Optional<HudRender> getRenderSettings(PlayerEntity player) {
		return this.asHudRendered().map(x -> x.getRenderSettings(this, player));
	}

	public Optional<Boolean> shouldRender(PlayerEntity player) {
		return this.asHudRendered().map(x -> x.shouldRender(this, player));
	}

	public Optional<Float> getFill(PlayerEntity player) {
		return this.asHudRendered().map(x -> x.getFill(this, player));
	}

	//Active Power

	@SuppressWarnings("unchecked")
	public Optional<IActivePower<C>> asActive() {
		return this.getFactory() instanceof IActivePower<?> hudRenderedPower ? Optional.of((IActivePower<C>) hudRenderedPower) : Optional.empty();
	}

	public boolean activate(PlayerEntity player) {
		Optional<IActivePower<C>> ciActivePower = this.asActive();
		ciActivePower.ifPresent(x -> x.activate(this, player));
		return ciActivePower.isPresent();
	}

	public Optional<IActivePower.Key> getKey(PlayerEntity player) {
		return this.asActive().map(x -> x.getKey(this, player));
	}
}
