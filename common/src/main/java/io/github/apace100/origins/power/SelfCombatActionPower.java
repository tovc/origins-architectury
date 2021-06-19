package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.power.CooldownPowerFactory;
import io.github.apace100.origins.power.configuration.ConditionedCombatActionConfiguration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

import static io.github.apace100.origins.registry.ModPowers.SELF_ACTION_ON_HIT;
import static io.github.apace100.origins.registry.ModPowers.SELF_ACTION_ON_KILL;

public class SelfCombatActionPower extends CooldownPowerFactory.Simple<ConditionedCombatActionConfiguration> {

	public static void onHit(PlayerEntity player, LivingEntity target, DamageSource source, float amount) {
		OriginComponent.getPowers(player, SELF_ACTION_ON_HIT.get()).forEach(x -> x.getFactory().execute(x, player, target, source, amount));
	}

	public static void onKill(PlayerEntity player, LivingEntity target, DamageSource source, float amount) {
		OriginComponent.getPowers(player, SELF_ACTION_ON_KILL.get()).forEach(x -> x.getFactory().execute(x, player, target, source, amount));
	}

	public SelfCombatActionPower() {
		super(ConditionedCombatActionConfiguration.CODEC);
	}

	public void execute(ConfiguredPower<ConditionedCombatActionConfiguration, ?> configuration, PlayerEntity player, LivingEntity target, DamageSource source, float amount) {
		if (configuration.getConfiguration().check(target, source, amount) && canUse(configuration, player)) {
			configuration.getConfiguration().entityAction().execute(player);
			use(configuration, player);
		}
	}
}
