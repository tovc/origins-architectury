package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredDamageCondition;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;

public class InvulnerablePower extends PowerFactory<FieldConfiguration<ConfiguredDamageCondition<?, ?>>> {

	public static boolean isInvulnerableTo(Entity entity, DamageSource source) {
		return OriginComponent.getPowers(entity, ModPowers.INVULNERABILITY.get()).stream().anyMatch(x -> x.getConfiguration().value().check(source, Float.NaN));
	}

	public InvulnerablePower() {
		super(FieldConfiguration.codec(ConfiguredDamageCondition.CODEC, "damage_condition"));
	}
}
