package io.github.apace100.origins.registry;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.util.OriginLootCondition;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class ModLoot {
	public static final LootItemConditionType ORIGIN_LOOT_CONDITION = registerLootCondition("origin", new OriginLootCondition.Serializer());

	private static LootItemConditionType registerLootCondition(String path, Serializer<? extends LootItemCondition> serializer) {
		return Registry.register(Registry.LOOT_CONDITION_TYPE, Origins.identifier(path), new LootItemConditionType(serializer));
	}

	public static void register() {}
}
