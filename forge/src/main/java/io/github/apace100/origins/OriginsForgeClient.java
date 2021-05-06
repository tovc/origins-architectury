package io.github.apace100.origins;

import io.github.apace100.origins.screen.GameHudRender;
import io.github.apace100.origins.util.HudRender;
import me.shedaniel.architectury.event.events.GuiEvent;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class OriginsForgeClient {
	public static void initialize() {
		OriginsClient.register();
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> OriginsForgeClient::buildConfigScreen);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(OriginsForgeClient::clientSetup);
		MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggedInEvent event) -> OriginsClient.isServerRunningOrigins = OriginsForge.channel.isRemotePresent(event.getNetworkManager()));
		MinecraftForge.EVENT_BUS.addListener(OriginsForgeClient::drawHud);
	}

	private static void clientSetup(FMLClientSetupEvent event) {
		OriginsClient.setup();
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
