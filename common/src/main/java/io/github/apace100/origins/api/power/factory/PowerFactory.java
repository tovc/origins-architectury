package io.github.apace100.origins.api.power.factory;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.IFactory;
import io.github.apace100.origins.api.power.PowerData;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import me.shedaniel.architectury.core.RegistryEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Map;
import java.util.function.Supplier;

public abstract class PowerFactory<T extends IOriginsFeatureConfiguration> extends RegistryEntry<PowerFactory<?>> implements Codec<ConfiguredPower<T, ?>> {
	public static final Codec<PowerFactory<?>> CODEC = OriginsRegistries.codec(OriginsRegistries.POWER_FACTORY);

	private static <T extends IOriginsFeatureConfiguration, F> Codec<Pair<T, PowerData>> powerCodec(Codec<T> codec) {
		return RecordCodecBuilder.create(instance -> instance.group(
				IFactory.asMap(codec).forGetter(Pair::getFirst),
				PowerData.CODEC.forGetter(Pair::getSecond)
		).apply(instance, Pair::new));
	}

	private final Codec<Pair<T, PowerData>> codec;
	private final boolean allowConditions;
	private boolean ticking = false;
	private boolean tickingWhenInactive = false;

	protected PowerFactory(Codec<T> codec) {
		this(codec, true);
	}

	/**
	 * Creates a new power factory.
	 *
	 * @param codec The codec used to serialize the configuration of this power.
	 * @param allowConditions Determines whether this power will use the global field {@link PowerData#conditions()} or not.
	 * @see #PowerFactory(Codec) for a version with allow conditions true by default.
	 */
	protected PowerFactory(Codec<T> codec, boolean allowConditions) {
		this.codec = powerCodec(codec);
		this.allowConditions = allowConditions;
	}
	/**
	 * Marks this power has having a ticking function, if this isn't done,
	 * the mod won't bother calling the {@link #tick(ConfiguredPower, PlayerEntity)} function.
	 *
	 * @param whenInactive If true, tick will bypass the check to {@link #isActive(ConfiguredPower, PlayerEntity)}
	 *
	 * @see #ticking() for a version that sets whenInactive to false.
	 */
	protected final void ticking(boolean whenInactive) {
		this.ticking = true;
		this.tickingWhenInactive = whenInactive;
	}

	/**
	 * Marks this power has having a ticking function, if this isn't done,
	 * the mod won't bother calling the {@link #tick(ConfiguredPower, PlayerEntity)} function.
	 *
	 * @see #ticking(boolean) for a version that allows ticking when this power is inactive.
	 */
	protected final void ticking() {
		this.ticking(false);
	}

	/**
	 * Returns a map containing children of this power.<br/>
	 * Origins uses this for the "multiple" power type, which contains children.
	 *
	 * @param configuration The configuration of this power.
	 *
	 * @return A map containing children of this power.
	 */
	public Map<String, ConfiguredPower<?, ?>> getContainedPowers(ConfiguredPower<T, ?> configuration) {
		return ImmutableMap.of();
	}

	@Override
	public <T1> DataResult<Pair<ConfiguredPower<T, ?>, T1>> decode(DynamicOps<T1> ops, T1 input) {
		return this.codec.decode(ops, input).map(pair -> pair.mapFirst(data -> this.configure(data.getFirst(), data.getSecond())));
	}

	@Override
	public <T1> DataResult<T1> encode(ConfiguredPower<T, ?> input, DynamicOps<T1> ops, T1 prefix) {
		return this.codec.encode(Pair.of(input.getConfiguration(), input.getData()), ops, prefix);
	}

	public ConfiguredPower<T, ?> configure(T input, PowerData data) {
		return new ConfiguredPower<>(this, input, data);
	}

	protected boolean shouldCheckConditions() {
		return this.allowConditions;
	}

	protected boolean shouldTickWhenActive() {
		return this.ticking;
	}

	protected boolean shouldTickWhenInactive() {
		return this.tickingWhenInactive;
	}

	public boolean canTick(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return this.shouldTickWhenActive() && (this.shouldTickWhenInactive() || this.isActive(configuration, player));
	}

	public void tick(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		this.tick(configuration.getConfiguration(), player);
	}

	public void onChosen(ConfiguredPower<T, ?> configuration, PlayerEntity player, boolean isOrbOfOrigin) {
		this.onChosen(configuration.getConfiguration(), player, isOrbOfOrigin);
	}

	public void onLost(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		this.onLost(configuration.getConfiguration(), player);
	}

	public void onAdded(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		this.onAdded(configuration.getConfiguration(), player);
	}

	public void onRemoved(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		this.onRemoved(configuration.getConfiguration(), player);
	}

	public void onRespawn(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		this.onRespawn(configuration.getConfiguration(), player);
	}

	public int tickInterval(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return this.tickInterval(configuration.getConfiguration(), player);
	}

	protected void tick(T configuration, PlayerEntity player) { }

	protected void onChosen(T configuration, PlayerEntity player, boolean isOrbOfOrigin) { }

	protected void onLost(T configuration, PlayerEntity player) { }

	protected void onAdded(T configuration, PlayerEntity player) { }

	protected void onRemoved(T configuration, PlayerEntity player) {}

	protected void onRespawn(T configuration, PlayerEntity player) { }

	protected int tickInterval(T configuration, PlayerEntity player) { return 1; }

	public boolean isActive(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return !this.shouldCheckConditions() || configuration.getData().conditions().stream().allMatch(condition -> condition.check(player));
	}

	public Tag serialize(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return new CompoundTag();
	}

	public void deserialize(ConfiguredPower<T, ?> configuration, PlayerEntity player, Tag tag) {

	}
}
