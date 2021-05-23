package io.github.apace100.origins.power.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.PowerTypeRegistry;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import java.util.function.Predicate;

public class PowerCondition implements Predicate<LivingEntity> {

	public static final Codec<PowerCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("power").forGetter(x -> x.power)
	).apply(instance, PowerCondition::new));

	private final Identifier power;

	public PowerCondition(Identifier power) {this.power = power;}

	@Override
	public boolean test(LivingEntity entity) {
		try {
			PowerType<?> powerType = PowerTypeRegistry.get(this.power);
			return ModComponentsArchitectury.getOriginComponent(entity).hasPower(powerType);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
