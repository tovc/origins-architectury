package io.github.apace100.origins.condition.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

import java.util.Optional;

public class AttackerCondition implements DamageCondition {

	public static final Codec<AttackerCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.ENTITY_CONDITION.optionalFieldOf("entity_condition").forGetter(x -> x.condition)
	).apply(instance, AttackerCondition::new));

	private final Optional<ConditionFactory.Instance<LivingEntity>> condition;

	public AttackerCondition(Optional<ConditionFactory.Instance<LivingEntity>> condition) {
		this.condition = condition;
	}

	@Override
	public boolean test(DamageSource source, float f) {
		Entity attacker = source.getAttacker();
		return attacker instanceof LivingEntity ? condition.map(x -> x.test((LivingEntity) attacker)).orElse(true) : Boolean.valueOf(false);
	}
}
