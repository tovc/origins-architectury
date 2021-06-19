package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;

import java.util.function.Predicate;

public class SubmergedInCondition implements Predicate<LivingEntity> {
	public static final Codec<SubmergedInCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.FLUID_TAG.fieldOf("fluid").forGetter(x -> x.fluid)
	).apply(instance, SubmergedInCondition::new));

	private final Tag<Fluid> fluid;

	public SubmergedInCondition(Tag<Fluid> fluid) {this.fluid = fluid;}

	@Override
	public boolean test(LivingEntity entity) {
		return entity.isSubmergedIn(fluid);
	}
}
