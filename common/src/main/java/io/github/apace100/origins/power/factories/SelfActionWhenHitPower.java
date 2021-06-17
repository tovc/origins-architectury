package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredDamageCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.power.CooldownPowerFactory;
import io.github.apace100.origins.power.configuration.power.ActionWhenHitConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

public class SelfActionWhenHitPower extends CooldownPowerFactory.Simple<ActionWhenHitConfiguration> {
	public static void execute(PlayerEntity player, DamageSource damageSource, float amount) {
		OriginComponent.getPowers(player, ModPowers.SELF_ACTION_WHEN_HIT.get()).forEach(x -> x.getFactory().whenHit(x, player, damageSource, amount));
	}

	public SelfActionWhenHitPower() {
		super(ActionWhenHitConfiguration.CODEC);
	}

	public void whenHit(ConfiguredPower<ActionWhenHitConfiguration, ?> configuration, PlayerEntity player, DamageSource damageSource, float damageAmount) {
		if (ConfiguredDamageCondition.check(configuration.getConfiguration().damageCondition(), damageSource, damageAmount)) {
			if (this.canUse(configuration, player)) {
				configuration.getConfiguration().entityAction().execute(player);
				this.use(configuration, player);
			}
		}
	}
}
