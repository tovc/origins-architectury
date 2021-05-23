package io.github.apace100.origins.power.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;

import java.util.function.Predicate;

public class FluidHeightCondition implements Predicate<LivingEntity> {
	public static Codec<FluidHeightCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(x -> x.comparison),
			Codec.DOUBLE.fieldOf("compare_to").forGetter(x -> x.compareTo),
			OriginsCodecs.FLUID_TAG.fieldOf("fluid").forGetter(x -> x.fluid)
	).apply(instance, FluidHeightCondition::new));

	private final Comparison comparison;
	private final double compareTo;
	private final Tag<Fluid> fluid;

	public FluidHeightCondition(Comparison comparison, double compareTo, Tag<Fluid> fluid) {
		this.comparison = comparison;
		this.compareTo = compareTo;
		this.fluid = fluid;
	}

	@Override
	public boolean test(LivingEntity t) {
		return comparison.compare(t.getFluidHeight(this.fluid), compareTo);
	}
}
