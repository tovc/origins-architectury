package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.ActionOnItemUseConfiguration;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ActionOnItemUsePower extends PowerFactory<ActionOnItemUseConfiguration> {
	public static void execute(PlayerEntity player, ItemStack stack, ItemStack target) {
		OriginComponent component = ModComponentsArchitectury.getOriginComponent(player);
		component.getPowers(ModPowers.ACTION_ON_ITEM_USE.get()).stream()
				.filter(x -> x.getFactory().doesApply(x, stack))
				.forEach(x -> x.getFactory().executeActions(x, player, target));
	}

	public ActionOnItemUsePower() {
		super(ActionOnItemUseConfiguration.CODEC);
	}

	public boolean doesApply(ConfiguredPower<ActionOnItemUseConfiguration, ?> factory, ItemStack stack) {
		ActionOnItemUseConfiguration configuration = factory.getConfiguration();
		return configuration.itemCondition() == null || configuration.itemCondition().check(stack);
	}

	public void executeActions(ConfiguredPower<ActionOnItemUseConfiguration, ?> factory, PlayerEntity player, ItemStack stack) {
		ActionOnItemUseConfiguration configuration = factory.getConfiguration();
		if (configuration.itemAction() != null)
			configuration.itemAction().execute(stack);
		if (configuration.entityAction() != null)
			configuration.entityAction().execute(player);
	}
}
