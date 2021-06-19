package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.power.CooldownPower;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.VariableIntPower;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.LivingEntity;

import java.util.function.Predicate;

public class ResourceCondition implements Predicate<LivingEntity> {
	public static Codec<ResourceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(x -> x.comparison),
			Codec.INT.fieldOf("compare_to").forGetter(x -> x.compareTo),
			OriginsCodecs.POWER_TYPE.fieldOf("resource").forGetter(x -> x.power)
	).apply(instance, ResourceCondition::new));

	private final Comparison comparison;
	private final int compareTo;
	private final PowerType<?> power;

	public ResourceCondition(Comparison comparison, int compareTo, PowerType<?> power) {
		this.comparison = comparison;
		this.compareTo = compareTo;
		this.power = power;
	}

	@Override
	public boolean test(LivingEntity t) {
		int resourceValue = 0;
		OriginComponent component = ModComponentsArchitectury.getOriginComponent(t);
		Power p = component.getPower(this.power);
		if (p instanceof VariableIntPower)
			resourceValue = ((VariableIntPower) p).getValue();
		else if (p instanceof CooldownPower)
			resourceValue = ((CooldownPower) p).getRemainingTicks();
		return this.comparison.compare(resourceValue, this.compareTo);
	}
}
