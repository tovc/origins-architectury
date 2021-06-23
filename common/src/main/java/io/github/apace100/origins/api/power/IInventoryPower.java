package io.github.apace100.origins.api.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerFactory;

public interface IInventoryPower<T extends IOriginsFeatureConfiguration> {
	boolean shouldDropOnDeath(ConfiguredPower<T, ?> configuration, PlayerEntity player, ItemStack stack);

	boolean shouldDropOnDeath(ConfiguredPower<T, ?> configuration, PlayerEntity player);

	Inventory getInventory(ConfiguredPower<T, ?> configuration, PlayerEntity player);

	ScreenHandlerFactory getMenuCreator(ConfiguredPower<T, ?> configuration, PlayerEntity player);
}
