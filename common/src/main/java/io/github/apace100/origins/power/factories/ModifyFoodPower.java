package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.ListConfiguration;
import io.github.apace100.origins.power.configuration.power.ModifyFoodConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import io.github.apace100.origins.util.AttributeUtil;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModifyFoodPower extends PowerFactory<ModifyFoodConfiguration> {

	public static double apply(PlayerEntity source, ItemStack stack, double baseValue, Function<ModifyFoodConfiguration, ListConfiguration<EntityAttributeModifier>> access) {
		List<EntityAttributeModifier> modifiers = OriginComponent.getPowers(source, ModPowers.MODIFY_FOOD.get()).stream()
				.filter(x -> x.getFactory().check(x, stack))
				.flatMap(x -> access.apply(x.getConfiguration()).getContent().stream()).collect(Collectors.toList());
		return AttributeUtil.applyModifiers(modifiers, baseValue);
	}

	public static void execute(PlayerEntity source, ItemStack stack) {
		OriginComponent.getPowers(source, ModPowers.MODIFY_FOOD.get()).stream()
				.filter(x -> x.getFactory().check(x, stack))
				.forEach(x -> x.getFactory().execute(x, source));
	}

	public ModifyFoodPower() {
		super(ModifyFoodConfiguration.CODEC);
	}

	public boolean check(ConfiguredPower<ModifyFoodConfiguration, ?> config, ItemStack stack) {
		return ConfiguredItemCondition.check(config.getConfiguration().itemCondition(), stack);
	}

	public void execute(ConfiguredPower<ModifyFoodConfiguration, ?> config, PlayerEntity player) {
		ConfiguredEntityAction.execute(config.getConfiguration().entityAction(), player);
	}
}
