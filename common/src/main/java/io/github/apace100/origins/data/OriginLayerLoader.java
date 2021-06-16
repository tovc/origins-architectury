package io.github.apace100.origins.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.IOriginsDynamicRegistryManager;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.event.OriginLayerLoadingEvent;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.api.registry.OriginsDynamicRegistries;
import io.github.apace100.origins.util.MultiJsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.RegistryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OriginLayerLoader extends MultiJsonDataLoader {
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	public OriginLayerLoader() {
		super(GSON, "origin_layers");
	}

	@Override
	protected void apply(Map<Identifier, List<JsonElement>> object, ResourceManager resourceManager, Profiler profiler) {
		IOriginsDynamicRegistryManager registryManager = OriginsDynamicRegistries.get(OriginsAPI.getServer());
		if (registryManager == null)
			throw new IllegalStateException("Tried to load origin layers before initializing dynamic registries!");
		MutableRegistry<OriginLayer> layers = registryManager.get(OriginsDynamicRegistries.ORIGIN_LAYER_KEY);
		object.forEach((identifier, jsonElements) -> {
			List<OriginLayer> originLayers = jsonElements.stream().flatMap(x -> {
				DataResult<OriginLayer> layer = OriginLayer.CODEC.decode(JsonOps.INSTANCE, x).map(Pair::getFirst);
				return layer.resultOrPartial(error -> Origins.LOGGER.error("Error loading origin layer \"{}\": {}", identifier, error)).stream();
			}).toList();
			OriginLayer baseLayer = null;
			List<OriginLayer> additiveLayers = new ArrayList<>();
			for (OriginLayer origin : originLayers) {
				if (baseLayer == null || origin.replace()) {
					baseLayer = origin;
					additiveLayers.clear();
				} else
					additiveLayers.add(origin);
			}
			if (baseLayer == null)
				Origins.LOGGER.error("Error loading origin layer \"{}\": No valid JSON could be loaded.", identifier);
			else {
				OriginLayer build = additiveLayers.stream().reduce(baseLayer.copyOf(), OriginLayer.Builder::merge, OriginLayer.Builder::merge).build().complete(identifier);
				OriginLayerLoadingEvent event = new OriginLayerLoadingEvent(identifier, build);
				ActionResult act = OriginLayerLoadingEvent.ORIGIN_LAYER_LOADING.invoker().act(event);
				if (act.isAccepted() || act == ActionResult.PASS) {
					layers.add(RegistryKey.of(layers.getKey(), identifier), event.getBuilder().build(), Lifecycle.experimental());
				}
			}
		});
	}
}
