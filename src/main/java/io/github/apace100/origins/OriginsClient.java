package io.github.apace100.origins;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.apace100.apoli.ApoliClient;
import io.github.apace100.origins.networking.ModPacketsS2C;
import io.github.apace100.origins.registry.ModBlocks;
import io.github.apace100.origins.registry.ModEntities;
import io.github.apace100.origins.screen.ViewOriginScreen;
import io.github.apace100.origins.util.OriginsConfigSerializer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import org.lwjgl.glfw.GLFW;

public class OriginsClient implements ClientModInitializer {

    public static KeyMapping usePrimaryActivePowerKeybind;
    public static KeyMapping useSecondaryActivePowerKeybind;
    public static KeyMapping viewCurrentOriginKeybind;

    public static ClientConfig config;

    public static boolean isServerRunningOrigins = false;

    @Override
    @Environment(EnvType.CLIENT)
    public static void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TEMPORARY_COBWEB, RenderType.cutout());

        EntityRendererRegistry.INSTANCE.register(ModEntities.ENDERIAN_PEARL,
            (context) -> new ThrownItemRenderer(context));

        ModPacketsS2C.register();

        AutoConfig.register(ClientConfig.class, OriginsConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();

        usePrimaryActivePowerKeybind = new KeyMapping("key.origins.primary_active", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "category." + Origins.MODID);
        useSecondaryActivePowerKeybind = new KeyMapping("key.origins.secondary_active", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category." + Origins.MODID);
        viewCurrentOriginKeybind = new KeyMapping("key.origins.view_origin", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, "category." + Origins.MODID);

        ApoliClient.registerPowerKeybinding("key.origins.primary_active", usePrimaryActivePowerKeybind);
        ApoliClient.registerPowerKeybinding("key.origins.secondary_active", useSecondaryActivePowerKeybind);
        ApoliClient.registerPowerKeybinding("primary", usePrimaryActivePowerKeybind);
        ApoliClient.registerPowerKeybinding("secondary", useSecondaryActivePowerKeybind);

        // "none" is the default key used when none is specified.
        ApoliClient.registerPowerKeybinding("none", usePrimaryActivePowerKeybind);

        KeyBindingHelper.registerKeyBinding(usePrimaryActivePowerKeybind);
        KeyBindingHelper.registerKeyBinding(useSecondaryActivePowerKeybind);
        KeyBindingHelper.registerKeyBinding(viewCurrentOriginKeybind);

        ClientTickEvents.START_CLIENT_TICK.register(tick -> {
            while(viewCurrentOriginKeybind.consumeClick()) {
                if(!(Minecraft.getInstance().screen instanceof ViewOriginScreen)) {
                    Minecraft.getInstance().setScreen(new ViewOriginScreen());
                }
            }
        });
    }

    @Config(name = Origins.MODID)
    public static class ClientConfig implements ConfigData {

        public int xOffset = 0;
        public int yOffset = 0;

        @ConfigEntry.BoundedDiscrete(max = 1)
        public float phantomizedOverlayStrength = 0.8F;

        @Override
        public void validatePostLoad() {
            if (phantomizedOverlayStrength < 0F) {
                phantomizedOverlayStrength = 0F;
            } else if (phantomizedOverlayStrength > 1F) {
                phantomizedOverlayStrength = 1F;
            }
        }
    }
}
