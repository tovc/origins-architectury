package io.github.apace100.origins.api;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.registry.OriginsDynamicRegistries;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;

public class OriginsAPI {
	public static final String MODID = "conditionedOrigins";

	public static Identifier identifier(String val) {
		return new Identifier(MODID, val);
	}

	@ExpectPlatform
	public static MinecraftServer getServer() {
		throw new AssertionError();
	}

	public static IOriginsDynamicRegistryManager getDynamicRegistries() {
		return OriginsDynamicRegistries.get(getServer());
	}

	public static Registry<OriginLayer> getLayers() {
		return getDynamicRegistries().get(OriginsDynamicRegistries.ORIGIN_LAYER_KEY);
	}

	public static Registry<Origin> getOrigins() {
		return getDynamicRegistries().get(OriginsDynamicRegistries.ORIGIN_KEY);
	}

	public static Registry<ConfiguredPower<?, ?>> getPowers() {
		return getDynamicRegistries().get(OriginsDynamicRegistries.CONFIGURED_POWER_KEY);
	}

	//The component will be a dummy if the entity isn't a player.
	@NotNull
	public static OriginComponent getComponent(Entity entity) {
		return ModComponentsArchitectury.getOriginComponent(entity);
	}
}
