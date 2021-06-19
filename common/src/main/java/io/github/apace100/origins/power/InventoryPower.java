package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.IActivePower;
import io.github.apace100.origins.api.power.IInventoryPower;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.InventoryConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;

import java.util.function.Function;

public class InventoryPower extends PowerFactory<InventoryConfiguration> implements IInventoryPower<InventoryConfiguration>, IActivePower<InventoryConfiguration> {

	private final int size;
	private final Function<Inventory, ScreenHandlerFactory> handler;

	public InventoryPower(int size, Function<Inventory, ScreenHandlerFactory> handler) {
		super(InventoryConfiguration.CODEC);
		this.size = size;
		this.handler = handler;
	}

	@Override
	public void activate(ConfiguredPower<InventoryConfiguration, ?> configuration, PlayerEntity player) {
		if (!player.world.isClient)
			player.openHandledScreen(new SimpleNamedScreenHandlerFactory(this.getMenuCreator(configuration, player), new TranslatableText(configuration.getConfiguration().name())));
	}

	@Override
	public Key getKey(ConfiguredPower<InventoryConfiguration, ?> configuration, PlayerEntity player) {
		return configuration.getConfiguration().key();
	}

	@Override
	public boolean shouldDropOnDeath(ConfiguredPower<InventoryConfiguration, ?> configuration, PlayerEntity player, ItemStack stack) {
		return this.shouldDropOnDeath(configuration, player) && ConfiguredItemCondition.check(configuration.getConfiguration().dropFilter(), stack);
	}

	@Override
	public boolean shouldDropOnDeath(ConfiguredPower<InventoryConfiguration, ?> configuration, PlayerEntity player) {
		return configuration.getConfiguration().dropOnDeath();
	}

	@Override
	public Inventory getInventory(ConfiguredPower<InventoryConfiguration, ?> configuration, PlayerEntity player) {
		return this.getData(configuration, player);
	}

	@Override
	public ScreenHandlerFactory getMenuCreator(ConfiguredPower<InventoryConfiguration, ?> configuration, PlayerEntity player) {
		return this.handler.apply(this.getData(configuration, player));
	}

	protected SimpleInventory getData(ConfiguredPower<InventoryConfiguration, ?> configuration, PlayerEntity player) {
		return configuration.getPowerData(player, () -> new SimpleInventory(this.size));
	}

	@Override
	public Tag serialize(ConfiguredPower<InventoryConfiguration, ?> configuration, PlayerEntity player) {
		SimpleInventory data = this.getData(configuration, player);
		DefaultedList<ItemStack> stacks = DefaultedList.ofSize(data.size(), ItemStack.EMPTY);
		for (int i = 0; i < data.size(); i++)
			stacks.set(i, data.getStack(i));
		return Inventories.toTag(new CompoundTag(), stacks);
	}

	@Override
	public void deserialize(ConfiguredPower<InventoryConfiguration, ?> configuration, PlayerEntity player, Tag tag) {
		if (tag instanceof CompoundTag compoundTag) {
			SimpleInventory data = this.getData(configuration, player);
			DefaultedList<ItemStack> stacks = DefaultedList.ofSize(data.size(), ItemStack.EMPTY);
			Inventories.fromTag(compoundTag, stacks);
			for (int i = 0; i < data.size(); i++)
				data.setStack(i, stacks.get(i));
		}
	}
}
