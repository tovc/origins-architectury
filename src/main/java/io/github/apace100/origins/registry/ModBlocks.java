package io.github.apace100.origins.registry;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.block.TemporaryCobwebBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

public class ModBlocks {

    public static final Block TEMPORARY_COBWEB = new TemporaryCobwebBlock(FabricBlockSettings.of(Material.WEB).noCollission().requiresCorrectToolForDrops().strength(4.0F));

    public static void register() {
        register("temporary_cobweb", TEMPORARY_COBWEB, false);
    }

    private static void register(String blockName, Block block) {
        register(blockName, block, true);
    }

    private static void register(String blockName, Block block, boolean withBlockItem) {
        Registry.register(Registry.BLOCK, new ResourceLocation(Origins.MODID, blockName), block);
        if(withBlockItem) {
            Registry.register(Registry.ITEM, new ResourceLocation(Origins.MODID, blockName), new BlockItem(block, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
        }
    }
}