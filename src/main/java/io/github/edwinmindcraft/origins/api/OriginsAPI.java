package io.github.edwinmindcraft.origins.api;

import io.github.edwinmindcraft.calio.api.CalioAPI;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class OriginsAPI {
	public static final String MODID = "origins";

	public static Registry<Origin> getOriginsRegistry(MinecraftServer server) {
		return CalioAPI.getDynamicRegistries(server).get(OriginsDynamicRegistries.ORIGINS_REGISTRY);
	}

	public static Registry<Origin> getOriginsRegistry() {
		return CalioAPI.getDynamicRegistries().get(OriginsDynamicRegistries.ORIGINS_REGISTRY);
	}

	public static Registry<OriginLayer> getLayersRegistry(MinecraftServer server) {
		return CalioAPI.getDynamicRegistries(server).get(OriginsDynamicRegistries.LAYERS_REGISTRY);
	}

	public static Registry<OriginLayer> getLayersRegistry() {
		return CalioAPI.getDynamicRegistries().get(OriginsDynamicRegistries.LAYERS_REGISTRY);
	}

	public static List<OriginLayer> getActiveLayers() {
		return getLayersRegistry().stream().filter(OriginLayer::enabled).sorted().toList();
	}
}
