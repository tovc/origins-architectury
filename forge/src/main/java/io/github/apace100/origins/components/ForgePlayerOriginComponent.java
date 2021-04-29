package io.github.apace100.origins.components;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.component.PlayerOriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.power.InventoryPower;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.registry.forge.ModComponentsArchitecturyImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class ForgePlayerOriginComponent extends PlayerOriginComponent implements ICapabilityProvider, ICapabilitySerializable<Tag>, IItemHandlerModifiable {

	private IItemHandlerModifiable wrapper = new CombinedInvWrapper();

	public ForgePlayerOriginComponent(PlayerEntity player) {
		super(player);
	}

	private final transient LazyOptional<OriginComponent> thisOptional = LazyOptional.of(() -> this);
	private final transient LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> this);

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
		LazyOptional<T> opt = ModComponentsArchitecturyImpl.ORIGIN_COMPONENT_CAPABILITY.orEmpty(capability, this.thisOptional);
		if (opt.isPresent())
			return opt;
		return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, this.itemHandler);
	}

	@Override
	public Tag serializeNBT() {
		return ModComponentsArchitecturyImpl.ORIGIN_COMPONENT_CAPABILITY.writeNBT(this, null);
	}

	@Override
	public void deserializeNBT(Tag arg) {
		ModComponentsArchitecturyImpl.ORIGIN_COMPONENT_CAPABILITY.readNBT(this, null, arg);
	}

	@Override
	public void setOrigin(OriginLayer layer, Origin origin) {
		boolean hasChanged = this.getOrigin(layer) != origin;
		super.setOrigin(layer, origin);
		if (hasChanged)
			rebuildCache();
	}

	private void rebuildCache() {
		IItemHandlerModifiable[] invPowers = getPowers().stream()
				.filter(x -> x instanceof Inventory)
				.sorted(Comparator.comparing(x -> x.getType().getIdentifier()))
				.map(Inventory.class::cast)
				.map(InvWrapper::new)
				.toArray(IItemHandlerModifiable[]::new);
		wrapper = new CombinedInvWrapper(invPowers);
	}

	@Override
	public void setStackInSlot(int i, @NotNull ItemStack arg) {
		this.wrapper.setStackInSlot(i, arg);
	}

	@Override
	public int getSlots() {
		return this.wrapper.getSlots();
	}

	@NotNull
	@Override
	public ItemStack getStackInSlot(int i) {
		return this.wrapper.getStackInSlot(i);
	}

	@NotNull
	@Override
	public ItemStack insertItem(int i, @NotNull ItemStack arg, boolean bl) {
		return this.wrapper.insertItem(i, arg, bl);
	}

	@NotNull
	@Override
	public ItemStack extractItem(int i, int j, boolean bl) {
		return this.wrapper.extractItem(i, j, bl);
	}

	@Override
	public int getSlotLimit(int i) {
		return this.wrapper.getSlotLimit(i);
	}

	@Override
	public boolean isItemValid(int i, @NotNull ItemStack arg) {
		return this.wrapper.isItemValid(i, arg);
	}
}
