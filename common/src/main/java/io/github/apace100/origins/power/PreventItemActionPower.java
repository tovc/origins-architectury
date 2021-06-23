package io.github.apace100.origins.power;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class PreventItemActionPower extends PowerFactory<FieldConfiguration<Optional<ConfiguredItemCondition<?, ?>>>> {

	public static boolean isUsagePrevented(Entity entity, ItemStack stack) {
		return OriginComponent.getPowers(entity, ModPowers.PREVENT_ITEM_USAGE.get()).stream().anyMatch(x -> x.getFactory().doesPrevent(x, stack));
	}

	public PreventItemActionPower() {
		super(FieldConfiguration.optionalCodec(ConfiguredItemCondition.CODEC, "item_condition"));
	}

	public boolean doesPrevent(ConfiguredPower<FieldConfiguration<Optional<ConfiguredItemCondition<?, ?>>>, ?> configuration, ItemStack stack) {
		return (!stack.isFood() || !Origins.config.disableFoodRestrictions) && configuration.getConfiguration().value().map(x -> x.check(stack)).orElse(true);
	}
}
