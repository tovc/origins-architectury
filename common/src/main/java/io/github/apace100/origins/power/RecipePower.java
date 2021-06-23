package io.github.apace100.origins.power;

import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.util.codec.RecipeCodec;
import net.minecraft.recipe.Recipe;

public class RecipePower extends PowerFactory<FieldConfiguration<Recipe<?>>> {

	public RecipePower() {
		super(FieldConfiguration.codec(RecipeCodec.CODEC, "recipe"));
	}
}
