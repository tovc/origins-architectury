package io.github.apace100.origins.factory.condition;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.factory.MetaFactories;
import io.github.apace100.origins.condition.item.EnchantmentCondition;
import io.github.apace100.origins.condition.item.IngredientCondition;
import io.github.apace100.origins.factory.meta.condition.IntComparingCondition;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;

import java.util.function.Predicate;

public class ItemConditions {

	public static void register() {
		MetaFactories.defineMetaConditions(ModRegistriesArchitectury.ITEM_CONDITION, OriginsCodecs.ITEM_CONDITION);
		register("food", ItemStack::isFood);
		register("ingredient", IngredientCondition.CODEC);
		register("armor_value", IntComparingCondition.codec(value -> value.getItem() instanceof ArmorItem ? ((ArmorItem) value.getItem()).getProtection() : 0));
		register("harvest_level", IntComparingCondition.codec(value -> (value.getItem() instanceof ToolItem) ? ((ToolItem) value.getItem()).getMaterial().getMiningLevel() : 0));
		register("enchantment", EnchantmentCondition.CODEC);
		register("meat", stack -> stack.isFood() && stack.getItem().getFoodComponent().isMeat());
	}

	private static void register(String name, Codec<? extends Predicate<ItemStack>> codec) {
		ModRegistriesArchitectury.ITEM_CONDITION.registerSupplied(Origins.identifier(name), () -> new ConditionFactory<>(codec));
	}

	private static void register(String name, Predicate<ItemStack> predicate) {
		register(name, Codec.unit(predicate));
	}
}
