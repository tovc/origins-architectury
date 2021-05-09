package io.github.apace100.origins.power.condition.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.fluid.FluidState;

import java.util.function.Predicate;

public class FluidCondition implements Predicate<CachedBlockPosition> {

	public static final Codec<FluidCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.FLUID_CONDITION.fieldOf("block").forGetter(x -> x.condition)
	).apply(instance, FluidCondition::new));

	private final ConditionFactory.Instance<FluidState> condition;

	public FluidCondition(ConditionFactory.Instance<FluidState> condition) {this.condition = condition;}

	@Override
	public boolean test(CachedBlockPosition cachedBlockPosition) {
		return this.condition.test(cachedBlockPosition.getBlockState().getFluidState());
	}
}
