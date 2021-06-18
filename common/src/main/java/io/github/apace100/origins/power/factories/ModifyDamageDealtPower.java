package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredDamageCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.power.ValueModifyingPowerFactory;
import io.github.apace100.origins.power.configuration.power.ModifyDamageDealtConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.Nullable;

public class ModifyDamageDealtPower extends ValueModifyingPowerFactory<ModifyDamageDealtConfiguration> {
	public static float modifyMelee(Entity entity, LivingEntity target, DamageSource source, float amount) {
		return OriginComponent.modify(entity, ModPowers.MODIFY_DAMAGE_DEALT.get(), amount, x -> x.getFactory().check(x, target, source, amount), x -> x.getFactory().execute(x, entity, target));
	}

	public static float modifyProjectile(Entity entity, LivingEntity target, DamageSource source, float amount) {
		return OriginComponent.modify(entity, ModPowers.MODIFY_PROJECTILE_DAMAGE.get(), amount, x -> x.getFactory().check(x, target, source, amount), x -> x.getFactory().execute(x, entity, target));
	}

	public ModifyDamageDealtPower() {
		super(ModifyDamageDealtConfiguration.CODEC);
	}

	public boolean check(ConfiguredPower<ModifyDamageDealtConfiguration, ?> config, @Nullable LivingEntity target, DamageSource source, float amount) {
		ModifyDamageDealtConfiguration configuration = config.getConfiguration();
		return ConfiguredDamageCondition.check(configuration.damageCondition(), source, amount) && (target == null || ConfiguredEntityCondition.check(configuration.targetCondition(), target));
	}

	public void execute(ConfiguredPower<ModifyDamageDealtConfiguration, ?> config, Entity entity, @Nullable LivingEntity target) {
		ModifyDamageDealtConfiguration configuration = config.getConfiguration();
		ConfiguredEntityAction.execute(configuration.selfAction(), entity);
		if (target != null)
			ConfiguredEntityAction.execute(configuration.targetAction(), target);
	}
}
