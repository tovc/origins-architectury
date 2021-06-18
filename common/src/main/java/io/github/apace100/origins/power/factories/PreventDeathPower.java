package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredDamageCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.power.PreventDeathConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

import java.util.Optional;

public class PreventDeathPower extends PowerFactory<PreventDeathConfiguration> {

	public static boolean tryPreventDeath(LivingEntity entity, DamageSource source, float amount) {
		Optional<ConfiguredPower<PreventDeathConfiguration, PreventDeathPower>> first = OriginComponent.getPowers(entity, ModPowers.PREVENT_DEATH.get()).stream()
				.filter(x -> ConfiguredDamageCondition.check(x.getConfiguration().condition(), source, amount)).findFirst();
		first.ifPresent(x -> {
			entity.setHealth(1.0F);
			ConfiguredEntityAction.execute(x.getConfiguration().action(), entity);
		});
		return first.isPresent();
	}

	public PreventDeathPower() {
		super(PreventDeathConfiguration.CODEC);
	}
}
