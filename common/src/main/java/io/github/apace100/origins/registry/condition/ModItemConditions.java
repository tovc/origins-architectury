package io.github.apace100.origins.registry.condition;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemCondition;
import io.github.apace100.origins.api.power.factory.ItemCondition;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import io.github.apace100.origins.condition.item.*;
import io.github.apace100.origins.factory.MetaFactories;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class ModItemConditions {

	public static final BiPredicate<ConfiguredItemCondition<?, ?>, ItemStack> PREDICATE = (config, stack) -> config.check(stack);

	public static final RegistrySupplier<SimpleItemCondition> FOOD = register("food", () -> new SimpleItemCondition(ItemStack::isFood));
	public static final RegistrySupplier<SimpleItemCondition> MEAT = register("meat", () -> new SimpleItemCondition(stack -> stack.isFood() && stack.getItem().getFoodComponent().isMeat()));
	public static final RegistrySupplier<IngredientCondition> INGREDIENT = register("ingredient", IngredientCondition::new);
	public static final RegistrySupplier<ComparingItemCondition> ARMOR_VALUE = register("armor_value", () -> new ComparingItemCondition(value -> value.getItem() instanceof ArmorItem ai ? ai.getProtection() : 0));
	public static final RegistrySupplier<ComparingItemCondition> HARVEST_LEVEL = register("harvest_level", () -> new ComparingItemCondition(value -> value.getItem() instanceof ToolItem ti ? ti.getMaterial().getMiningLevel() : 0));
	public static final RegistrySupplier<EnchantmentCondition> ENCHANTMENT = register("enchantment", EnchantmentCondition::new);

	public static void register() {
		MetaFactories.defineMetaConditions(OriginsRegistries.ITEM_CONDITION, DelegatedItemCondition::new, ConfiguredItemCondition.CODEC, PREDICATE);
	}

	@SuppressWarnings("unchecked")
	private static <T extends ItemCondition<?>> RegistrySupplier<T> register(String name, Supplier<T> factory) {
		return (RegistrySupplier<T>) OriginsRegistries.ITEM_CONDITION.registerSupplied(Origins.identifier(name), factory::get);
	}
}
