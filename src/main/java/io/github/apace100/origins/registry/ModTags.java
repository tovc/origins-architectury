package io.github.apace100.origins.registry;

import io.github.apace100.origins.Origins;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static final Tag<Item> MEAT = TagRegistry.item(new ResourceLocation(Origins.MODID, "meat"));
    public static final Tag<Block> UNPHASABLE = TagRegistry.block(new ResourceLocation(Origins.MODID, "unphasable"));
    public static final Tag<Block> NATURAL_STONE = TagRegistry.block(new ResourceLocation(Origins.MODID, "natural_stone"));
    public static final Tag<Item> RANGED_WEAPONS = TagRegistry.item(new ResourceLocation(Origins.MODID, "ranged_weapons"));

    public static void register() {

    }
}
