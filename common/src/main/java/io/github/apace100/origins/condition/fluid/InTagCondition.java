package io.github.apace100.origins.condition.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.Tag;

import java.util.function.Predicate;

public class InTagCondition implements Predicate<FluidState> {

	public static final Codec<InTagCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.FLUID_TAG.fieldOf("tag").forGetter(x -> x.tag)
	).apply(instance, InTagCondition::new));

	private final Tag<Fluid> tag;

	public InTagCondition(Tag<Fluid> tag) {this.tag = tag;}

	@Override
	public boolean test(FluidState fluid) {
		return fluid.isIn(this.tag);
	}
}
