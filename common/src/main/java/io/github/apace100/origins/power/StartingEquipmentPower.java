package io.github.apace100.origins.power;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.StartingInventoryConfiguration;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Comparator;

public class StartingEquipmentPower extends PowerFactory<StartingInventoryConfiguration> {
	public StartingEquipmentPower() {
		super(StartingInventoryConfiguration.CODEC, false);
	}

	@Override
	protected void onChosen(StartingInventoryConfiguration configuration, PlayerEntity player, boolean isOrbOfOrigin) {
		this.giveStacks(configuration, player);
	}

	@Override
	protected void onRespawn(StartingInventoryConfiguration configuration, PlayerEntity player) {
		if (configuration.recurrent())
			this.giveStacks(configuration, player);
	}

	private void giveStacks(StartingInventoryConfiguration configuration, PlayerEntity player) {
		configuration.stacks().getContent().stream().sorted(Comparator.reverseOrder()).forEach(x -> {
			Origins.LOGGER.info("Giving player {} stack: {}", player.getName().asString(), x.stack().toString());
			if (x.hasPosition() && player.inventory.getStack(x.position()).isEmpty())
				player.inventory.setStack(x.position(), x.stack().copy());
			else
				player.giveItemStack(x.stack().copy());
		});
	}
}
