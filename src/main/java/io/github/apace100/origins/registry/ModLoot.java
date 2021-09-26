package io.github.apace100.origins.registry;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.util.OriginLootCondition;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class ModLoot {

    private static final ResourceLocation DUNGEON_LOOT = new ResourceLocation("minecraft", "chests/simple_dungeon");
    private static final ResourceLocation STRONGHOLD_LIBRARY = new ResourceLocation("minecraft", "chests/stronghold_library");
    private static final ResourceLocation MINESHAFT = new ResourceLocation("minecraft", "chests/abandoned_mineshaft");
    private static final ResourceLocation WATER_RUIN = new ResourceLocation("minecraft", "chests/underwater_ruin_small");

    public static final LootItemConditionType ORIGIN_LOOT_CONDITION = registerLootCondition("origin", new OriginLootCondition.Serializer());

    private static LootItemConditionType registerLootCondition(String path, Serializer<? extends LootItemCondition> serializer) {
        return Registry.register(Registry.LOOT_CONDITION_TYPE, Origins.identifier(path), new LootItemConditionType(serializer));
    }

    public static void registerLootTables() {
        CompoundTag waterProtectionLevel1 = createEnchantmentTag(ModEnchantments.WATER_PROTECTION, 1);
        CompoundTag waterProtectionLevel2 = createEnchantmentTag(ModEnchantments.WATER_PROTECTION, 2);
        CompoundTag waterProtectionLevel3 = createEnchantmentTag(ModEnchantments.WATER_PROTECTION, 3);
        LootTableLoadingCallback.EVENT.register(((resourceManager, lootManager, identifier, fabricLootSupplierBuilder, lootTableSetter) -> {
            if(DUNGEON_LOOT.equals(identifier)) {
                FabricLootPoolBuilder lootPool = FabricLootPoolBuilder.builder()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                        .setWeight(20)
                        .apply(SetNbtFunction.setTag(waterProtectionLevel1)))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                        .setWeight(10)
                        .apply(SetNbtFunction.setTag(waterProtectionLevel2)))
                    .add(EmptyLootItem.emptyItem().setWeight(80));
                fabricLootSupplierBuilder.withPool(lootPool.build());
            } else if(STRONGHOLD_LIBRARY.equals(identifier)) {
                FabricLootPoolBuilder lootPool = FabricLootPoolBuilder.builder()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                        .setWeight(20)
                        .apply(SetNbtFunction.setTag(waterProtectionLevel2)))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                        .setWeight(10)
                        .apply(SetNbtFunction.setTag(waterProtectionLevel3)))
                    .add(EmptyLootItem.emptyItem().setWeight(80));
                fabricLootSupplierBuilder.withPool(lootPool.build());
            } else if(MINESHAFT.equals(identifier)) {
                FabricLootPoolBuilder lootPool = FabricLootPoolBuilder.builder()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                        .setWeight(20)
                        .apply(SetNbtFunction.setTag(waterProtectionLevel1)))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                        .setWeight(5)
                        .apply(SetNbtFunction.setTag(waterProtectionLevel2)))
                    .add(EmptyLootItem.emptyItem().setWeight(90));
                fabricLootSupplierBuilder.withPool(lootPool.build());
            } else if(WATER_RUIN.equals(identifier)) {
                FabricLootPoolBuilder lootPool = FabricLootPoolBuilder.builder()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                        .setWeight(10)
                        .apply(SetNbtFunction.setTag(waterProtectionLevel1)))
                    .add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                        .setWeight(20)
                        .apply(SetNbtFunction.setTag(waterProtectionLevel2)))
                    .add(EmptyLootItem.emptyItem().setWeight(110));
                fabricLootSupplierBuilder.withPool(lootPool.build());
            }
        }));
    }

    private static CompoundTag createEnchantmentTag(Enchantment enchantment, int level) {
        EnchantmentInstance entry = new EnchantmentInstance(enchantment, level);
        return EnchantedBookItem.createForEnchantment(entry).getTag();
    }
}
