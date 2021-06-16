package io.github.apace100.origins.component;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModOrigins;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.shedaniel.architectury.utils.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PlayerOriginComponent implements OriginComponent {
	private final Map<OriginLayer, Origin> origins;
	private final Map<Identifier, ConfiguredPower<?, ?>> powerCache;
	private final Map<Identifier, Object> dataContainers;
	private final PlayerEntity player;

	private boolean hadOriginBefore = false;

	public PlayerOriginComponent(PlayerEntity player) {
		this.player = player;
		this.origins = Util.make(() -> {
			Object2ObjectOpenHashMap<OriginLayer, Origin> map = new Object2ObjectOpenHashMap<>();
			map.defaultReturnValue(ModOrigins.EMPTY);
			return Object2ObjectMaps.synchronize(map);
		});
		this.powerCache = new ConcurrentHashMap<>();
		this.dataContainers = new ConcurrentHashMap<>();
	}

	@Override
	public boolean hasOrigin(OriginLayer layer) {
		synchronized (this.origins) {
			return !Objects.equals(ModOrigins.EMPTY, this.origins.get(layer));
		}
	}

	@Override
	public boolean hasAllOrigins() {
		return OriginsAPI.getLayers().stream().allMatch(this::hasOrigin);
	}

	@Override
	public Map<OriginLayer, Origin> getOrigins() {
		synchronized (this.origins) {
			return ImmutableMap.copyOf(this.origins);
		}
	}

	@Override
	public Origin getOrigin(OriginLayer layer) {
		synchronized (this.origins) {
			return this.origins.get(layer);
		}
	}

	@Override
	public boolean hadOriginBefore() {
		return this.hadOriginBefore;
	}

	@Override
	public boolean hasPower(ConfiguredPower<?, ?> powerType) {
		return this.hasPower(OriginsAPI.getPowers().getId(powerType));
	}

	@Override
	public boolean hasPower(Identifier powerType) {
		if (powerType == null) return false;
		return this.powerCache.containsKey(powerType);
	}

	@Override
	public @Nullable ConfiguredPower<?, ?> getPower(Identifier identifier) {
		return this.powerCache.get(identifier);
	}

	@Override
	public List<ConfiguredPower<?, ?>> getPowers() {
		return ImmutableList.copyOf(this.powerCache.values());
	}

	@Override
	public <T extends IOriginsFeatureConfiguration, F extends PowerFactory<T>> List<ConfiguredPower<T, F>> getPowers(F factory) {
		return this.getPowers(factory, false);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IOriginsFeatureConfiguration, F extends PowerFactory<T>> List<ConfiguredPower<T, F>> getPowers(F factory, boolean includeInactive) {
		return this.powerCache.values().stream().filter(x -> Objects.equals(factory, x.getFactory())).map(x -> (ConfiguredPower<T, F>) x)
				.filter(x -> includeInactive || x.isActive(this.player)).toList();
	}

	@Override
	public void setOrigin(OriginLayer layer, Origin origin) {
		synchronized (this.origins) {
			Origin previous = this.origins.put(layer, origin);
			assert previous != null; // Should never happen since I'm using an Obj2ObjMap with a default value.
			previous.powers().stream().filter(x -> !origin.powers().contains(x))
					.map(this.powerCache::remove).filter(Objects::nonNull)
					.forEach(removed -> {
						removed.onRemoved(this.player);
						removed.onLost(this.player);
					});
			origin.powers().forEach(identifier -> OriginsAPI.getPowers().getOrEmpty(identifier).ifPresentOrElse(
					power -> {
						this.powerCache.put(identifier, power);
						power.onAdded(this.player);
					},
					() -> Origins.LOGGER.warn("Tried to add missing power \"{}\" to \"{}\"", identifier, player.getName())
			));
			if (this.hasAllOrigins())
				this.hadOriginBefore = true;
		}
	}

	@Override
	public void serverTick() {
		this.powerCache.values().forEach(x -> x.tick(this.player));

		//FIXME: SimpleStatusEffectPower & StackingStatusEffectPower should be moved to the new system.
	}

	@Override
	public void readFromNbt(CompoundTag compoundTag) {
		this.fromTag(compoundTag, true);
	}

	@Override
	@Contract("null -> fail; _ -> param1")
	public CompoundTag writeToNbt(CompoundTag compoundTag) {
		Registry<OriginLayer> layerRegistry = OriginsAPI.getLayers();
		Registry<Origin> originsRegistry = OriginsAPI.getOrigins();
		CompoundTag origins = new CompoundTag();
		this.origins.forEach((layer, origin) -> {
			Identifier l = layerRegistry.getId(layer);
			if (l == null) {
				Origins.LOGGER.error("Cannot serialize unregistered layer \"{}\" for player: \"{}\"", layer, this.player);
				return;
			}
			Identifier o = originsRegistry.getId(origin);
			if (o == null) {
				Origins.LOGGER.error("Cannot serialize unregistered origin \"{}\" on layer \"{}\" for player: \"{}\"", origin, layer, this.player);
				return;
			}
			origins.putString(l.toString(), o.toString());
		});

		CompoundTag powers = new CompoundTag();
		this.powerCache.forEach((identifier, power) -> powers.put(identifier.toString(), power.serialize(this.player)));

		compoundTag.putBoolean("HadOriginBefore", this.hadOriginBefore);
		compoundTag.put("Origins", origins);
		compoundTag.put("Powers", powers);

		return compoundTag;
	}

	@Override
	public void applySyncPacket(PacketByteBuf buf) {
		CompoundTag compoundTag = buf.readCompoundTag();
		if (compoundTag != null)
			this.fromTag(compoundTag, false);
	}

	@Override
	public void sync() {
		OriginComponent.sync(this.player);
	}

	@Override
	@NotNull
	@SuppressWarnings("unchecked")
	public <T> T getPowerData(Identifier power, Supplier<? extends T> builder) {
		try {
			return (T) this.dataContainers.computeIfAbsent(power, t -> builder.get());
		} catch (ClassCastException cce) {
			this.dataContainers.remove(power);
			return (T) this.dataContainers.computeIfAbsent(power, t -> builder.get());
		}
	}

	@Override
	@NotNull
	public <T> T getPowerData(ConfiguredPower<?, ?> power, Supplier<? extends T> builder) {
		return this.getPowerData(OriginsAPI.getPowers().getId(power), builder);
	}

	private void fromTag(CompoundTag compoundTag, boolean callPowerOnAdd) {
		if (player == null) {
			Origins.LOGGER.error("Player was null in `fromTag`! This is a bug!");
			return; //Stop reading right here. There's no point in going forward with this.
		}
		if (callPowerOnAdd) {
			for (ConfiguredPower<?, ?> power : this.powerCache.values()) {
				power.onRemoved(this.player);
				power.onLost(this.player);
			}
		}
		this.powerCache.clear();
		synchronized (this.origins) {
			this.origins.clear();

			//Fabric: Support for worlds prior to the data system is dropped.
			//FIXME Make a DataFixer for 1.16 -> 1.17 worlds.
			this.hadOriginBefore = compoundTag.getBoolean("HadOriginBefore");
			Registry<OriginLayer> layerRegistry = OriginsAPI.getLayers();
			Registry<Origin> originsRegistry = OriginsAPI.getOrigins();
			Registry<ConfiguredPower<?, ?>> powerRegistry = OriginsAPI.getPowers();

			CompoundTag origins = compoundTag.getCompound("Origins");
			for (String layerName : origins.getKeys()) {
				Optional.ofNullable(Identifier.tryParse(layerName)).flatMap(layerRegistry::getOrEmpty).ifPresentOrElse(
						layer -> Optional.ofNullable(Identifier.tryParse(origins.getString(layerName))).flatMap(originsRegistry::getOrEmpty).ifPresentOrElse(
								origin -> {
									Identifier identifier = new Identifier(origins.getString(layerName));
									if (!origin.special() && layer.origins().noneMatch(identifier::equals)) {
										Origins.LOGGER.warn("Origin \"{}\" for isn't contained in layer \"{}\" for player: {}.", identifier, layerName, player.getName());
										origin = ModOrigins.EMPTY;
									}
									this.origins.put(layer, origin);
								},
								() -> Origins.LOGGER.warn("Couldn't find origin \"{}\" for layer \"{}\" for player: {}.", origins.getString(layerName), layerName, player.getName())
						),
						() -> Origins.LOGGER.warn("Couldn't find layer \"{}\" for player: {}.", layerName, player.getName())
				);
			}


			Set<Identifier> knownPowers = this.origins.values().stream().flatMap(origin -> origin.powers().stream()).collect(Collectors.toSet());
			if (compoundTag.contains("Powers", NbtType.COMPOUND)) {
				CompoundTag powers = compoundTag.getCompound("Powers");
				for (String key : powers.getKeys()) {
					Optional.ofNullable(Identifier.tryParse(key)).flatMap(powerRegistry::getOrEmpty).ifPresentOrElse(
							power -> {
								try {
									Identifier name = new Identifier(key);
									if (knownPowers.contains(name)) {
										power.deserialize(this.player, powers.get(key));
										this.powerCache.put(name, power);
										if (callPowerOnAdd)
											power.onAdded(this.player);
									} else
										Origins.LOGGER.debug("Removing power \"{}\" from player: {}", key, player.getName());
								} catch (Exception e) {
									Origins.LOGGER.warn("Deserialization of power \"{}\" failed for player: {} (Data:{})", key, player.getName(), powers.get(key));
									Origins.LOGGER.debug("Deserialization failed with error: ", e);
								}
							}, () -> Origins.LOGGER.warn("Couldn't find power \"{}\" for player: {}", key, player.getName())
					);
				}
			}

			knownPowers.forEach(power -> this.powerCache.computeIfAbsent(power, powerRegistry::get));
		}
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("OriginComponent[\n");
		this.powerCache.forEach((key, value) -> str.append("\t").append(key).append(": ").append(value.serialize(this.player).toString()).append("\n"));
		str.append("]");
		return str.toString();
	}
}
