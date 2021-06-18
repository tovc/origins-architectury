package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.FieldConfiguration;
import io.github.apace100.origins.util.codec.RecipeCodec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;

public class RecipePower extends PowerFactory<FieldConfiguration<Recipe<?>>> {

    public RecipePower() {
        super(FieldConfiguration.codec(RecipeCodec.CODEC, "recipe"));
    }
}
