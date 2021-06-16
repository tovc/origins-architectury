package io.github.apace100.origins.api.component;

import com.google.common.collect.ImmutableList;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.api.power.IValueModifyingPower;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.util.AttributeUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface OriginComponent {

	static void sync(PlayerEntity player) {
		ModComponentsArchitectury.syncOriginComponent(player);
	}

	static <T extends IOriginsFeatureConfiguration, F extends PowerFactory<T>> void withPower(Entity entity, F factory, Predicate<ConfiguredPower<T, F>> power, Consumer<ConfiguredPower<T, F>> with) {
		if (entity instanceof PlayerEntity)
			ModComponentsArchitectury.getOriginComponent(entity).getPowers(factory).stream().filter(p -> power == null || power.test(p)).findAny().ifPresent(with);
	}

	static <T extends IOriginsFeatureConfiguration, F extends PowerFactory<T>> List<ConfiguredPower<T, F>> getPowers(Entity entity, F factory) {
		if (entity instanceof PlayerEntity) {
			return ModComponentsArchitectury.getOriginComponent(entity).getPowers(factory);
		}
		return ImmutableList.of();
	}

	static <T extends IOriginsFeatureConfiguration, F extends PowerFactory<T>> boolean hasPower(Entity entity, F factory) {
		if (entity instanceof PlayerEntity player) {
			return ModComponentsArchitectury.getOriginComponent(entity).getPowers().stream().anyMatch(p -> Objects.equals(factory, p.getFactory()) && p.isActive(player));
		}
		return false;
	}

	static <T extends IOriginsFeatureConfiguration, F extends PowerFactory<T> & IValueModifyingPower<T>> float modify(Entity entity, F factory, float baseValue) {
		return (float) modify(entity, factory, (double) baseValue, null, null);
	}

	static <T extends IOriginsFeatureConfiguration, F extends PowerFactory<T> & IValueModifyingPower<T>> float modify(Entity entity, F factory, float baseValue, Predicate<ConfiguredPower<T, F>> powerFilter) {
		return (float) modify(entity, factory, (double) baseValue, powerFilter, null);
	}

	static <T extends IOriginsFeatureConfiguration, F extends PowerFactory<T> & IValueModifyingPower<T>> float modify(Entity entity, F factory, float baseValue, Predicate<ConfiguredPower<T, F>> powerFilter, Consumer<ConfiguredPower<T, F>> powerAction) {
		return (float) modify(entity, factory, (double) baseValue, powerFilter, powerAction);
	}

	static <T extends IOriginsFeatureConfiguration, F extends PowerFactory<T> & IValueModifyingPower<T>> double modify(Entity entity, F factory, double baseValue) {
		return modify(entity, factory, baseValue, null, null);
	}

	static <T extends IOriginsFeatureConfiguration, F extends PowerFactory<T> & IValueModifyingPower<T>> double modify(Entity entity, F factory, double baseValue, Predicate<ConfiguredPower<T, F>> powerFilter, Consumer<ConfiguredPower<T, F>> powerAction) {
		if (entity instanceof PlayerEntity player) {
			List<ConfiguredPower<T, F>> powers = ModComponentsArchitectury.getOriginComponent(entity).getPowers(factory).stream()
					.filter(p -> powerFilter == null || powerFilter.test(p)).toList();
			List<EntityAttributeModifier> mps = powers.stream().flatMap(p -> p.getFactory().getModifiers(p, player).stream()).collect(Collectors.toList());
			if (powerAction != null) powers.forEach(powerAction);
			return AttributeUtil.sortAndApplyModifiers(mps, baseValue);
		}
		return baseValue;
	}

	boolean hasOrigin(OriginLayer layer);

	boolean hasAllOrigins();

	//Outsource those components for usage with forge.

	Map<OriginLayer, Origin> getOrigins();

	Origin getOrigin(OriginLayer layer);

	boolean hadOriginBefore();

	boolean hasPower(ConfiguredPower<?, ?> powerType);

	boolean hasPower(Identifier powerType);

	/**
	 * If this entity has the power with the given identifier, this will return that power.
	 *
	 * @param identifier The identifier of the power to check.
	 *
	 * @return The power if this component contains it, {@code null} otherwise.
	 */
	@Nullable
	ConfiguredPower<?, ?> getPower(Identifier identifier);

	List<ConfiguredPower<?, ?>> getPowers();

	<T extends IOriginsFeatureConfiguration, F extends PowerFactory<T>> List<ConfiguredPower<T, F>> getPowers(F factory);

	<T extends IOriginsFeatureConfiguration, F extends PowerFactory<T>> List<ConfiguredPower<T, F>> getPowers(F factory, boolean includeInactive);

	void setOrigin(OriginLayer layer, Origin origin);

	void serverTick();

	void readFromNbt(CompoundTag compoundTag);

	@Contract("null -> fail; _ -> param1")
	CompoundTag writeToNbt(CompoundTag compoundTag);

	void applySyncPacket(PacketByteBuf buf);

	void sync();

	/**
	 * A way to store data for powers that would need to serialize a par of their data.
	 *
	 * @param power   The identifier of the power to get the container for.
	 * @param builder A function to create the data the first time.
	 * @param <T>     A data type. Should be mutable.
	 *
	 * @return If an instance already exists, it will be returned, otherwise, returns a new instance.
	 */
	@NotNull <T> T getPowerData(Identifier power, Supplier<@NotNull ? extends T> builder);

	/**
	 * A way to store data for powers that would need to serialize a par of their data.
	 *
	 * @param power   The instance of the power to get the container for.
	 * @param builder A function to create the data the first time.
	 * @param <T>     A data type. Should be mutable.
	 *
	 * @return If an instance already exists, it will be returned, otherwise, returns a new instance.
	 */
	@NotNull <T> T getPowerData(ConfiguredPower<?, ?> power, Supplier<@NotNull ? extends T> builder);

	default boolean checkAutoChoosingLayers(PlayerEntity player, boolean includeDefaults) {
		boolean choseOneAutomatically = false;
		ArrayList<OriginLayer> layers = new ArrayList<>();
		for (OriginLayer layer : OriginsAPI.getLayers()) {
			if (layer.enabled()) {
				layers.add(layer);
			}
		}
		Collections.sort(layers);
		for (OriginLayer layer : layers) {
			boolean shouldContinue = false;
			if (layer.enabled() && !hasOrigin(layer)) {
				if (includeDefaults && layer.hasDefaultOrigin()) {
					setOrigin(layer, OriginsAPI.getOrigins().get(layer.defaultOrigin()));
					choseOneAutomatically = true;
					shouldContinue = true;
				} else if (layer.optionCount(player) == 1 && layer.autoChoose()) {
					List<Origin> origins = layer.origins(player).map(Origin::get).filter(Origin::choosable).collect(Collectors.toList());
					if (origins.size() == 0) {
						List<Identifier> randomOrigins = layer.randomOrigins(player).toList();
						setOrigin(layer, Origin.get(randomOrigins.get(player.getRandom().nextInt(randomOrigins.size()))));
					} else {
						setOrigin(layer, origins.get(0));
					}
					choseOneAutomatically = true;
					shouldContinue = true;
				} else if (layer.optionCount(player) == 0) {
					shouldContinue = true;
				}
			} else {
				shouldContinue = true;
			}
			if (!shouldContinue) {
				break;
			}
		}
		return choseOneAutomatically;
	}
}
