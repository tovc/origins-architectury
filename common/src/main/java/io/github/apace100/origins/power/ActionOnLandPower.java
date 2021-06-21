package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.player.PlayerEntity;

public class ActionOnLandPower extends PowerFactory<FieldConfiguration<ConfiguredEntityAction<?, ?>>> {
	public static void execute(PlayerEntity player) {
		OriginComponent.getPowers(player, ModPowers.ACTION_ON_LAND.get()).forEach(x -> x.getFactory().executeAction(x, player));
	}

	public ActionOnLandPower() {
		super(FieldConfiguration.codec(ConfiguredEntityAction.CODEC, "action_on_land"));
	}

	public void executeAction(ConfiguredPower<FieldConfiguration<ConfiguredEntityAction<?, ?>>, ?> config, PlayerEntity player) {
		config.getConfiguration().value().execute(player);
	}
}
