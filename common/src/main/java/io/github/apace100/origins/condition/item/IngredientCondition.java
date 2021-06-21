package io.github.apace100.origins.condition.item;

import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.ItemCondition;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public class IngredientCondition extends ItemCondition<FieldConfiguration<Ingredient>> {

	public IngredientCondition() {
		super(FieldConfiguration.codec(OriginsCodecs.INGREDIENT, "ingredient"));
	}

	@Override
	public boolean check(FieldConfiguration<Ingredient> configuration, ItemStack stack) {
		return configuration.value().test(stack);
	}
}
