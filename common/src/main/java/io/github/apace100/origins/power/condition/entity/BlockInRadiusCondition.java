package io.github.apace100.origins.power.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;
import io.github.apace100.origins.util.Shape;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

import java.util.function.Predicate;

public class BlockInRadiusCondition implements Predicate<LivingEntity> {

	public static final Codec<BlockInRadiusCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.BLOCK_CONDITION.fieldOf("block_condition").forGetter(x -> x.blockCondition),
			Codec.INT.fieldOf("radius").forGetter(x -> x.radius),
			OriginsCodecs.SHAPE.optionalFieldOf("shape", Shape.CUBE).forGetter(x -> x.shape),
			Codec.INT.optionalFieldOf("compare_to", 1).forGetter(x -> x.compareTo),
			OriginsCodecs.COMPARISON.optionalFieldOf("comparison", Comparison.GREATER_THAN_OR_EQUAL).forGetter(x -> x.comparison)
	).apply(instance, BlockInRadiusCondition::new));

	private final ConditionFactory.Instance<CachedBlockPosition> blockCondition;
	private final int radius;
	private final Shape shape;
	private final int compareTo;
	private final Comparison comparison;

	public BlockInRadiusCondition(ConditionFactory.Instance<CachedBlockPosition> blockCondition, int radius, Shape shape, int compareTo, Comparison comparison) {
		this.blockCondition = blockCondition;
		this.radius = radius;
		this.shape = shape;
		this.compareTo = compareTo;
		this.comparison = comparison;
	}

	@Override
	public boolean test(LivingEntity entity) {
		int count = 0;
		int stopAt = this.comparison.getOptimalStoppingIndex(this.compareTo);
		for (BlockPos pos : Shape.getPositions(entity.getBlockPos(), shape, radius)) {
			if (blockCondition.test(new CachedBlockPosition(entity.world, pos, true))) {
				if (++count == stopAt) break;
			}
		}
		return comparison.compare(count, compareTo);
	}
}
