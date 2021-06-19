package io.github.apace100.origins;

import io.github.apace100.origins.power.ParticlePower;
import io.github.apace100.origins.registry.OriginsDynamicRegistryManager;
import me.shedaniel.architectury.event.events.TickEvent;
import me.shedaniel.architectury.event.events.client.ClientLifecycleEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class OriginClientEventHandler {
	@Environment(EnvType.CLIENT)
	public static void register() {
		TickEvent.PLAYER_PRE.register(OriginClientEventHandler::tick);
		ClientLifecycleEvent.CLIENT_SETUP.register(client -> OriginsDynamicRegistryManager.initializeClient());
	}

	@Environment(EnvType.CLIENT)
	private static void tick(PlayerEntity player) {
		if (player instanceof OtherClientPlayerEntity && !player.isInvisibleTo(MinecraftClient.getInstance().player))
			ParticlePower.renderParticles(player);
	}
}
