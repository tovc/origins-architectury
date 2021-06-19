package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredDamageCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.power.ValueModifyingPowerFactory;
import io.github.apace100.origins.power.configuration.ModifyDamageTakenConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class ModifyDamageTakenPower extends ValueModifyingPowerFactory<ModifyDamageTakenConfiguration> {
	public static float modify(Entity entity, DamageSource source, float amount) {
		return OriginComponent.modify(entity, ModPowers.MODIFY_DAMAGE_TAKEN.get(), amount, x -> x.getFactory().check(x, source, amount), x -> x.getFactory().execute(x, entity, source));
	}

	public ModifyDamageTakenPower() {
		super(ModifyDamageTakenConfiguration.CODEC);
	}

	public boolean check(ConfiguredPower<ModifyDamageTakenConfiguration, ?> config, DamageSource source, float amount) {
		ModifyDamageTakenConfiguration configuration = config.getConfiguration();
		return ConfiguredDamageCondition.check(configuration.damageCondition(), source, amount);
	}

	public void execute(ConfiguredPower<ModifyDamageTakenConfiguration, ?> config, Entity entity, DamageSource source) {
		ModifyDamageTakenConfiguration configuration = config.getConfiguration();
		ConfiguredEntityAction.execute(configuration.selfAction(), entity);
		if (source.getAttacker() != null && source.getAttacker() instanceof LivingEntity)
			ConfiguredEntityAction.execute(configuration.targetAction(), source.getAttacker());
	}
}
