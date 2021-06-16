package io.github.apace100.origins.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.IOriginsDynamicRegistryManager;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.event.PowerLoadingEvent;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.api.registry.OriginsDynamicRegistries;
import io.github.apace100.origins.util.MultiJsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.RegistryKey;

import java.util.*;

public class PowerLoader extends MultiJsonDataLoader {

	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private static final Comparator<ConfiguredPower<?, ?>> LOADING_ORDER_COMPARATOR = Comparator.comparingInt((ConfiguredPower<?, ?> x) -> x.getData().loadingPriority());

	/**
	 * Recursively registers all powers in the given file.
	 * @param registry The registry to register the powers into.
	 * @param identifier The identifier of the power to register.
	 * @param original The original {@link ConfiguredPower} to register.
	 */
	@SuppressWarnings("unchecked")
	private static void register(MutableRegistry<ConfiguredPower<?, ?>> registry, Identifier identifier, ConfiguredPower<?, ?> original) {
		PowerLoadingEvent event = new PowerLoadingEvent(identifier, original, original.getData().complete(identifier));
		ActionResult act = PowerLoadingEvent.POWER_LOADING.invoker().act(event);
		if (act.isAccepted() || act == ActionResult.PASS) {
			ConfiguredPower<?, ?> newPower = ((PowerFactory<IOriginsFeatureConfiguration>) original.getFactory()).configure(event.getOriginal().getConfiguration(), event.getBuilder().build());
			registry.add(RegistryKey.of(registry.getKey(), identifier), newPower, Lifecycle.experimental());
			newPower.getContainedPowers().forEach((s, configuredPower) -> register(registry, new Identifier(identifier.getNamespace(), identifier.getPath() + s), configuredPower));
		}
	}

	public PowerLoader() {
		super(GSON, "powers");
	}

	@Override
	protected void apply(Map<Identifier, List<JsonElement>> map, ResourceManager resourceManager, Profiler profiler) {
		IOriginsDynamicRegistryManager registryManager = OriginsDynamicRegistries.get(OriginsAPI.getServer());
		if (registryManager == null)
			throw new IllegalStateException("Tried to load powers before initializing dynamic registries!");
		MutableRegistry<ConfiguredPower<?, ?>> powers = registryManager.get(OriginsDynamicRegistries.CONFIGURED_POWER_KEY);
		map.forEach((identifier, jsonElements) -> {
			Optional<ConfiguredPower<?, ?>> definition = jsonElements.stream().flatMap(x -> {
				DataResult<ConfiguredPower<?, ?>> power = ConfiguredPower.CODEC.decode(JsonOps.INSTANCE, x).map(Pair::getFirst);
				Optional<ConfiguredPower<?, ?>> powerDefinition = power.resultOrPartial(error -> Origins.LOGGER.error("Error loading power \"{}\": {}", identifier, error));
				return powerDefinition.stream();
			}).max(LOADING_ORDER_COMPARATOR);
			//This may be breaking because subpowers of overridden powers will no longer be registered.
			//Fixing it should be easy, but that would be bad design, so not doing it.
			definition.ifPresentOrElse(def -> register(powers, identifier, def),
					() -> Origins.LOGGER.error("Loading for all instances of power \"{}\" failed. It won't be registered", identifier));
		});
	}
}
