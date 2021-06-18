package io.github.apace100.origins.registry;

import io.github.apace100.origins.Origins;
import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.architectury.registry.Registry;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;

import static net.minecraft.util.registry.Registry.*;

public class ModRegistriesArchitectury {
	public static final Lazy<Registries> REGISTRIES = new Lazy<>(() -> Registries.get(Origins.MODID));

	public static final Registry<Item> ITEMS;
	public static final Registry<Block> BLOCKS;
	public static final Registry<EntityType<?>> ENTITY_TYPES;
	public static final Registry<Enchantment> ENCHANTMENTS;
	public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZERS;

	public static final RegistrySupplier<EntityAttribute> SWIM_SPEED;

	static {
		Registries registries = REGISTRIES.get();

		ITEMS = registries.get(ITEM_KEY);
		BLOCKS = registries.get(BLOCK_KEY);
		ENTITY_TYPES = registries.get(ENTITY_TYPE_KEY);
		ENCHANTMENTS = registries.get(ENCHANTMENT_KEY);
		RECIPE_SERIALIZERS = registries.get(RECIPE_SERIALIZER_KEY);
		SWIM_SPEED = registries.get(ATTRIBUTE_KEY).delegateSupplied(new Identifier("forge", "swim_speed"));
	}
}
