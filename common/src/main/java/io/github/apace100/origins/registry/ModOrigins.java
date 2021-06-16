package io.github.apace100.origins.registry;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.origin.Impact;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.registry.OriginsBuiltinRegistries;
import io.github.apace100.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class ModOrigins {

	public static final RegistryKey<Origin> EMPTY_KEY = RegistryKey.of(OriginsDynamicRegistries.ORIGIN_KEY, Origins.identifier("empty"));

	public static final Origin EMPTY = createEmpty();

	private static Origin createEmpty() {
		return Registry.register(OriginsBuiltinRegistries.ORIGINS, EMPTY_KEY.getValue(), Origin.builder().withDisplay(ItemStack.EMPTY).withIdentifier(EMPTY_KEY.getValue()).withImpact(Impact.NONE).withOrder(-1).withLoadingPriority(Integer.MAX_VALUE).disableChoice().special().build());
	}

	public static void register() { }
}
