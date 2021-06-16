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
import io.github.apace100.origins.api.event.OriginLoadingEvent;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.registry.OriginsDynamicRegistries;
import io.github.apace100.origins.util.MultiJsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.RegistryKey;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OriginLoader extends MultiJsonDataLoader {
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private static final Comparator<Origin> LOADING_ORDER_COMPARATOR = Comparator.comparingInt(Origin::loadingPriority);

	public OriginLoader() {
		super(GSON, "conditionedOrigins");
	}

	@Override
	protected void apply(Map<Identifier, List<JsonElement>> map, ResourceManager resourceManager, Profiler profiler) {
		IOriginsDynamicRegistryManager registryManager = OriginsDynamicRegistries.get(OriginsAPI.getServer());
		if (registryManager == null)
			throw new IllegalStateException("Tried to load conditionedOrigins before initializing dynamic registries!");
		MutableRegistry<Origin> origins = registryManager.get(OriginsDynamicRegistries.ORIGIN_KEY);
		map.forEach((identifier, jsonElements) -> {
			Optional<Origin> definition = jsonElements.stream().flatMap(x -> {
				DataResult<Origin> origin = Origin.CODEC.decode(JsonOps.INSTANCE, x).map(Pair::getFirst);
				Optional<Origin> powerDefinition = origin.resultOrPartial(error -> Origins.LOGGER.error("Error loading origin \"{}\": {}", identifier, error));
				return powerDefinition.stream();
			}).max(LOADING_ORDER_COMPARATOR);
			definition.map(x -> x.complete(identifier)).ifPresentOrElse(def -> {
						OriginLoadingEvent event = new OriginLoadingEvent(identifier, def);
						ActionResult act = OriginLoadingEvent.ORIGIN_LOADING.invoker().act(event);
						if (act.isAccepted() || act == ActionResult.PASS)
							origins.add(RegistryKey.of(origins.getKey(), identifier), event.getBuilder().build(), Lifecycle.experimental());
					},
					() -> Origins.LOGGER.error("Loading for all instances of origin \"{}\" failed. It won't be registered", identifier));
		});
	}
}
