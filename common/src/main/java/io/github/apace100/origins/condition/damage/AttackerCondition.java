package io.github.apace100.origins.condition.damage;

import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import io.github.apace100.origins.api.power.factory.DamageCondition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

import java.util.Optional;

public class AttackerCondition extends DamageCondition<FieldConfiguration<Optional<ConfiguredEntityCondition<?, ?>>>> {

	public AttackerCondition() {
		super(FieldConfiguration.optionalCodec(ConfiguredEntityCondition.CODEC, "entity_condition"));
	}

	@Override
	protected boolean check(FieldConfiguration<Optional<ConfiguredEntityCondition<?, ?>>> configuration, DamageSource source, float amount) {
		Entity attacker = source.getAttacker();
		return attacker instanceof LivingEntity le ? configuration.value().map(x -> x.check(le)).orElse(true) : Boolean.valueOf(false);
	}
}
