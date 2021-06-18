package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.power.VariableIntPowerFactory;
import io.github.apace100.origins.power.configuration.power.DamageOverTimeConfiguration;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Difficulty;

import java.util.Map;

public class DamageOverTimePower extends VariableIntPowerFactory.Simple<DamageOverTimeConfiguration> {
	public DamageOverTimePower() {
		super(DamageOverTimeConfiguration.CODEC);
		this.ticking(true);
	}

	protected DataContainer getDataContainer(ConfiguredPower<DamageOverTimeConfiguration, ?> configuration, PlayerEntity player) {
		return configuration.getPowerData(player, () -> new DataContainer(configuration.getConfiguration().initialValue(), 0));
	}

	@Override
	protected int get(ConfiguredPower<DamageOverTimeConfiguration, ?> configuration, PlayerEntity player) {
		return this.getDataContainer(configuration, player).value;
	}

	@Override
	protected void set(ConfiguredPower<DamageOverTimeConfiguration, ?> configuration, PlayerEntity player, int value) {
		this.getDataContainer(configuration, player).value = value;
	}

	@Override
	public void tick(ConfiguredPower<DamageOverTimeConfiguration, ?> configuration, PlayerEntity player) {
		if (configuration.isActive(player))
			this.doDamage(configuration, player);
		else
			this.resetDamage(configuration, player);
	}

	protected void doDamage(ConfiguredPower<DamageOverTimeConfiguration, ?> configuration, PlayerEntity player) {
		DataContainer dataContainer = this.getDataContainer(configuration, player);
		dataContainer.outOfDamageTicks = 0;
		if (this.getValue(configuration, player) <= 0) {
			this.assign(configuration, player, configuration.getConfiguration().interval());
			player.damage(configuration.getConfiguration().damageSource(), player.world.getDifficulty() == Difficulty.EASY ? configuration.getConfiguration().damageEasy() : configuration.getConfiguration().damage());
		} else {
			this.decrement(configuration, player);
		}
	}

	protected void resetDamage(ConfiguredPower<DamageOverTimeConfiguration, ?> configuration, PlayerEntity player) {
		DataContainer dataContainer = this.getDataContainer(configuration, player);
		if (dataContainer.outOfDamageTicks >= 20)
			this.assign(configuration, player, this.getDamageBegin(configuration.getConfiguration(), player));
		else
			dataContainer.outOfDamageTicks++;
	}

	protected int getDamageBegin(DamageOverTimeConfiguration configuration, PlayerEntity player) {
		int prot = getProtection(configuration, player);
		if (prot >= 64)
			return 24000;
		prot = (int) (prot * 2 * 20 * configuration.protectionEffectiveness());
		return configuration.delay() + prot;
	}

	private int getProtection(DamageOverTimeConfiguration configuration, PlayerEntity player) {
		if (configuration.protectionEnchantment() == null) {
			return 0;
		} else {
			Map<EquipmentSlot, ItemStack> enchantedItems = configuration.protectionEnchantment().getEquipment(player);
			Iterable<ItemStack> iterable = enchantedItems.values();
			int i = 0;
			for (ItemStack itemStack : iterable)
				i += EnchantmentHelper.getLevel(configuration.protectionEnchantment(), itemStack);
			return i * enchantedItems.size();
		}
	}

	private static final class DataContainer {
		private int value;
		private int outOfDamageTicks;

		private DataContainer(int value, int outOfDamageTicks) {
			this.value = value;
			this.outOfDamageTicks = outOfDamageTicks;
		}
	}
}
