package io.github.apace100.origins.power.condition.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.function.Predicate;

public class IngredientCondition implements Predicate<ItemStack> {
	public static final Codec<IngredientCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.INGREDIENT.fieldOf("ingredient").forGetter(x -> x.ingredient)
	).apply(instance, IngredientCondition::new));

	private final Ingredient ingredient;

	public IngredientCondition(Ingredient ingredient) {this.ingredient = ingredient;}

	@Override
	public boolean test(ItemStack itemStack) {
		return this.ingredient.test(itemStack);
	}
}
