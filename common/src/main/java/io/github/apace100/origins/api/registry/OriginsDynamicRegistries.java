package io.github.apace100.origins.api.registry;

import io.github.apace100.origins.api.IOriginsDynamicRegistryManager;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.registry.OriginsDynamicRegistryManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class OriginsDynamicRegistries {
	public static final RegistryKey<Registry<ConfiguredPower<?, ?>>> CONFIGURED_POWER_KEY = RegistryKey.ofRegistry(OriginsAPI.identifier("configured_powers"));
	public static final RegistryKey<Registry<Origin>> ORIGIN_KEY = RegistryKey.ofRegistry(OriginsAPI.identifier("conditionedOrigins"));
	public static final RegistryKey<Registry<OriginLayer>> ORIGIN_LAYER_KEY = RegistryKey.ofRegistry(OriginsAPI.identifier("layers"));

	/**
	 * Returns the currently active {@link IOriginsDynamicRegistryManager}.
	 * If the input is {@code null} and this is called on the client, it will return
	 * the client version of this {@link IOriginsDynamicRegistryManager} instead.
	 *
	 * @param server
	 *
	 * @return
	 */
	@Nullable
	@Contract("!null -> !null")
	public static IOriginsDynamicRegistryManager get(@Nullable MinecraftServer server) {
		return OriginsDynamicRegistryManager.getInstance(server);
	}
}
