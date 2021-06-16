package io.github.apace100.origins.api.registry;

import com.mojang.serialization.Lifecycle;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public class OriginsBuiltinRegistries {
	public static final Registry<ConfiguredPower<?, ?>> CONFIGURED_POWERS = create(OriginsDynamicRegistries.CONFIGURED_POWER_KEY, Lifecycle.stable());
	public static final Registry<Origin> ORIGINS = create(OriginsDynamicRegistries.ORIGIN_KEY, Lifecycle.stable());
	public static final Registry<OriginLayer> ORIGIN_LAYERS = create(OriginsDynamicRegistries.ORIGIN_LAYER_KEY, Lifecycle.stable());

	@SuppressWarnings("unchecked")
	private static <T> Registry<T> create(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
		return Registry.register(((Registry<MutableRegistry<?>>) Registry.REGISTRIES), key.getValue(), new SimpleRegistry<>(key, lifecycle));
	}
}
