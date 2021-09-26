package io.github.apace100.origins.origin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * @deprecated Use {@link OriginsAPI#getLayersRegistry()} where possible instead.
 */
@Deprecated
public class OriginLayers {
	public static OriginLayer getLayer(ResourceLocation id) {
		return OriginsAPI.getLayersRegistry().getOptional(id).map(OriginLayer::new).orElse(null);
	}

	public static Collection<OriginLayer> getLayers() {
		return OriginsAPI.getLayersRegistry().stream().map(OriginLayer::new).collect(ImmutableSet.toImmutableSet());
	}

	public static int size() {
		return OriginsAPI.getLayersRegistry().keySet().size();
	}

	/**
	 * @deprecated Dead code.
	 */
	@Deprecated
	public static void clear() {}

	/**
	 * @deprecated Dead code.
	 */
	@Deprecated
	public static void add(OriginLayer layer) {}
}
