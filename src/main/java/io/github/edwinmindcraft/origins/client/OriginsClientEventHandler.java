package io.github.edwinmindcraft.origins.client;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.screen.ChooseOriginScreen;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Origins.MODID)
public class OriginsClientEventHandler {

	@SubscribeEvent
	public static void renderTick(TickEvent.RenderTickEvent event) {
		if (event.phase == TickEvent.Phase.START && OriginsClient.DISPLAY_ORIGIN_SCREEN) {
			Minecraft instance = Minecraft.getInstance();
			if (instance.screen != null || instance.player == null)
				return;
			IOriginContainer.get(instance.player).ifPresent(container -> {
				List<OriginLayer> layers = OriginsAPI.getActiveLayers().stream().filter(x -> !container.hasOrigin(x)).toList();
				layers.sort(OriginLayer::compareTo);
				instance.setScreen(new ChooseOriginScreen(layers, 0, OriginsClient.SHOW_DIRT_BACKGROUND));
			});
		}
	}
}
