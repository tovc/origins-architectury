package io.github.edwinmindcraft.origins.api.capabilies;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.Optional;

public interface IOriginContainer extends INBTSerializable<Tag> {
	/**
	 * Sets the origin for the given layer.
	 *
	 * @param layer  The layer to set the origin for.
	 * @param origin The origin to given to the player.
	 */
	void setOrigin(OriginLayer layer, Origin origin);

	/**
	 * Returns the origin for the given layer, or {@link Origin#EMPTY} if no origin was set for that layer.
	 *
	 * @param layer The layer to get the origin of.
	 *
	 * @return The origin for the given layer.
	 */
	Origin getOrigin(OriginLayer layer);

	/**
	 * Checks if the player has an origin for the given layer.
	 *
	 * @param layer The layer to check.
	 *
	 * @return {@code true} if the layer has an assigned origin, {@code false} if the origin is {@link Origin#EMPTY}.
	 */
	boolean hasOrigin(OriginLayer layer);

	/**
	 * Checks if this player has all origins currently assigned.
	 *
	 * @return {@code false} if any layer is empty, {@code true otherwise}.
	 */
	default boolean hasAllOrigins() {
		return OriginsAPI.getActiveLayers().stream().allMatch(this::hasOrigin);
	}

	/**
	 * Returns {@code true} if the player had all origins set at some point, {@code false} otherwise.
	 */
	boolean hadAllOrigins();

	/**
	 * Returns a read-only {@link Map} that contains all the layers and origins for the given player.
	 */
	Map<OriginLayer, Origin> getOrigins();

	default boolean checkAutoChoosingLayers(Player player, boolean includeDefaults) {
		boolean choseOneAutomatically = false;
		Registry<Origin> registry = OriginsAPI.getOriginsRegistry();
		for (OriginLayer layer : OriginsAPI.getActiveLayers()) {
			boolean shouldContinue = false;
			if (!this.hasOrigin(layer)) {
				Origin def;
				if (includeDefaults && layer.hasDefaultOrigin() && (def = registry.get(layer.defaultOrigin())) != null) {
					this.setOrigin(layer, def);
					choseOneAutomatically = true;
					shouldContinue = true;
				} else {
					Optional<Origin> automaticOrigin = layer.getAutomaticOrigin(player);
					if (automaticOrigin.isPresent()) {
						this.setOrigin(layer, automaticOrigin.get());
						choseOneAutomatically = true;
						shouldContinue = true;
					} else if (layer.getOriginOptionCount(player) == 0)
						shouldContinue = true;
				}
			} else
				shouldContinue = true;
			if (!shouldContinue)
				break;
		}
		return choseOneAutomatically;
	}
}
