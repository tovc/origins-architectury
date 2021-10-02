package io.github.apace100.origins.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.github.apace100.origins.registry.ModLoot;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class OriginLootCondition implements LootItemCondition {
	private final ResourceLocation origin;

	private OriginLootCondition(ResourceLocation origin) {
		this.origin = origin;
	}

	public LootItemConditionType getType() {
		return ModLoot.ORIGIN_LOOT_CONDITION;
	}

	public boolean test(LootContext lootContext) {
		Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
		if (entity == null) return false;
		return IOriginContainer.get(entity).map(container -> container.getOrigins().values().stream().map(ForgeRegistryEntry::getRegistryName).anyMatch(this.origin::equals)).orElse(false);
	}

	public static LootItemCondition.Builder builder(String originId) {
		return builder(new ResourceLocation(originId));
	}

	public static LootItemCondition.Builder builder(ResourceLocation origin) {
		return () -> new OriginLootCondition(origin);
	}

	public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<OriginLootCondition> {
		public void serialize(JsonObject jsonObject, OriginLootCondition originLootCondition, JsonSerializationContext context) {
			jsonObject.addProperty("origin", originLootCondition.origin.toString());
		}

		public OriginLootCondition deserialize(JsonObject jsonObject, JsonDeserializationContext context) {
			return new OriginLootCondition(new ResourceLocation(GsonHelper.getAsString(jsonObject, "origin")));
		}
	}
}
