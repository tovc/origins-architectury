package io.github.apace100.origins.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;

public class FabricPlayerOriginComponent extends PlayerOriginComponent implements AutoSyncedComponent, ServerTickingComponent {
	public FabricPlayerOriginComponent(PlayerEntity player) {
		super(player);
	}

	@Override
	public void writeToNbt(CompoundTag compoundTag) {
		super.writeToNbt(compoundTag);
	}
}
