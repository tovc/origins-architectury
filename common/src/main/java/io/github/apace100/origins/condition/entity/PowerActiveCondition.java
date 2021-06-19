package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.LivingEntity;

import java.util.function.Predicate;

public class PowerActiveCondition implements Predicate<LivingEntity> {

	public static final Codec<PowerActiveCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.POWER_TYPE.fieldOf("power").forGetter(x -> x.type)
	).apply(instance, PowerActiveCondition::new));

	private final PowerType<?> type;

	public PowerActiveCondition(PowerType<?> type) {this.type = type;}

	@Override
	public boolean test(LivingEntity livingEntity) {
		return type.isActive(livingEntity);
	}
}
