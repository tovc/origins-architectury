package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.power.RestrictArmorConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.stream.Stream;

public class RestrictArmorPower extends PowerFactory<RestrictArmorConfiguration> {
	public static boolean isForbidden(PlayerEntity player, EquipmentSlot slot, ItemStack stack) {
		return Stream.concat(OriginComponent.getPowers(player, ModPowers.CONDITIONED_RESTRICT_ARMOR.get()).stream(), OriginComponent.getPowers(player, ModPowers.RESTRICT_ARMOR.get()).stream())
				.anyMatch(x -> !x.getConfiguration().check(slot, stack));
	}

	public RestrictArmorPower() {
		super(RestrictArmorConfiguration.CODEC, false);
	}

	public boolean canEquip(ConfiguredPower<RestrictArmorConfiguration, ?> configuration, ItemStack stack, EquipmentSlot slot) {
		return configuration.getConfiguration().check(slot, stack);
	}

	@Override
	protected void onChosen(RestrictArmorConfiguration configuration, PlayerEntity player, boolean isOrbOfOrigin) {
		configuration.dropIllegalItems(player);
	}
}
