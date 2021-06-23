package io.github.apace100.origins.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.api.component.OriginComponent;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class ModComponentsArchitectury {
	@ExpectPlatform
	public static OriginComponent getOriginComponent(Entity entity) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void syncOriginComponent(Entity player) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void syncWith(ServerPlayerEntity player, Entity provider) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Optional<OriginComponent> maybeGetOriginComponent(Entity player) {
		throw new AssertionError();
	}
}
