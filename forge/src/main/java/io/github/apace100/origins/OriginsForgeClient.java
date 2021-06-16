package io.github.apace100.origins;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.screen.ChooseOriginScreen;
import io.github.apace100.origins.screen.GameHudRender;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class OriginsForgeClient {
	public static void initialize() {
		OriginsClient.register();
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> OriginsForgeClient::buildConfigScreen);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(OriginsForgeClient::clientSetup);
		MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggedInEvent event) -> {
			OriginsClient.isServerRunningOrigins = OriginsForge.channel.isRemotePresent(event.getNetworkManager());
			OriginsForge.SHOULD_QUEUE_SCREEN = true;
		});
		MinecraftForge.EVENT_BUS.addListener(OriginsForgeClient::drawHud);
		MinecraftForge.EVENT_BUS.addListener(OriginsForgeClient::openOriginsScreen);
	}

	private static void clientSetup(FMLClientSetupEvent event) {
		OriginsClient.setup();
	}

	private static void openOriginsScreen(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.START && event.side == LogicalSide.CLIENT && OriginsForge.SHOULD_QUEUE_SCREEN) {
			MinecraftClient.getInstance().submitAsync(() -> {
				MinecraftClient instance = MinecraftClient.getInstance();
				Screen currentScreen = instance.currentScreen;
				if (OriginsForge.SHOULD_QUEUE_SCREEN && currentScreen == null && instance.player != null && instance.world != null) {
					OriginComponent component = ModComponentsArchitectury.getOriginComponent(instance.player);
					ArrayList<OriginLayer> layers = OriginLayers.getLayers().stream().filter(layer -> layer.isEnabled() && !component.hasOrigin(layer)).sorted().collect(Collectors.toCollection(ArrayList::new));
					if (layers.isEmpty()) {
						OriginsForge.SHOULD_QUEUE_SCREEN = false;
						return; //Should be useless, but networking isn't the most accurate science.
					}
					instance.openScreen(new ChooseOriginScreen(layers, 0, true));
					OriginsForge.SHOULD_QUEUE_SCREEN = false;
				}
			});
		}
	}

	private static void drawHud(RenderGameOverlayEvent.Pre event) {
		//Draw only at the beginning.
		if (event.getType() != RenderGameOverlayEvent.ElementType.ALL)
			return;
		for (GameHudRender hudRender : GameHudRender.HUD_RENDERS) {
			hudRender.render(event.getMatrixStack(), event.getPartialTicks());
		}
	}

	private static Screen buildConfigScreen(MinecraftClient minecraftClient, Screen parent) {
		return AutoConfig.getConfigScreen(OriginsClient.ClientConfig.class, parent).get();
	}
}
