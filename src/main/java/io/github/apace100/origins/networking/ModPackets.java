package io.github.apace100.origins.networking;

import io.github.apace100.origins.Origins;
import net.minecraft.resources.ResourceLocation;

public class ModPackets {


    public static final ResourceLocation OPEN_ORIGIN_SCREEN = new ResourceLocation(Origins.MODID, "open_origin_screen");
    public static final ResourceLocation CHOOSE_ORIGIN = new ResourceLocation(Origins.MODID, "choose_origin");
    public static final ResourceLocation ORIGIN_LIST = new ResourceLocation(Origins.MODID, "origin_list");
    public static final ResourceLocation LAYER_LIST = new ResourceLocation(Origins.MODID, "layer_list");
    public static final ResourceLocation CHOOSE_RANDOM_ORIGIN = new ResourceLocation(Origins.MODID, "choose_random_origin");
    public static final ResourceLocation CONFIRM_ORIGIN = Origins.identifier("confirm_origin");
}
