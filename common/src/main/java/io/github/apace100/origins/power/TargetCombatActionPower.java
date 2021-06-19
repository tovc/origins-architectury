package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.power.CooldownPowerFactory;
import io.github.apace100.origins.power.configuration.ConditionedCombatActionConfiguration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

import static io.github.apace100.origins.registry.ModPowers.TARGET_ACTION_ON_HIT;

public class TargetCombatActionPower extends CooldownPowerFactory.Simple<ConditionedCombatActionConfiguration> {

	public static void onHit(PlayerEntity player, LivingEntity target, DamageSource source, float amount) {
		OriginComponent.getPowers(player, TARGET_ACTION_ON_HIT.get()).forEach(x -> x.getFactory().execute(x, player, target, source, amount));
	}

	public TargetCombatActionPower() {
		super(ConditionedCombatActionConfiguration.CODEC);
	}

	public void execute(ConfiguredPower<ConditionedCombatActionConfiguration, ?> configuration, PlayerEntity player, LivingEntity target, DamageSource source, float amount) {
		if (configuration.getConfiguration().check(target, source, amount) && canUse(configuration, player)) {
			configuration.getConfiguration().entityAction().execute(target);
			use(configuration, player);
		}
	}
}
