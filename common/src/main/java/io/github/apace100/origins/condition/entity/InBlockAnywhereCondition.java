package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.function.Predicate;

public class InBlockAnywhereCondition implements Predicate<LivingEntity> {

	public static final Codec<InBlockAnywhereCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("compare_to", 1).forGetter(x -> x.compareTo),
			OriginsCodecs.COMPARISON.optionalFieldOf("comparison", Comparison.GREATER_THAN_OR_EQUAL).forGetter(x -> x.comparison),
			OriginsCodecs.BLOCK_CONDITION.fieldOf("block_condition").forGetter(x -> x.blockCondition)
	).apply(instance, InBlockAnywhereCondition::new));

	private final int compareTo;
	private final Comparison comparison;
	private final ConditionFactory.Instance<CachedBlockPosition> blockCondition;

	public InBlockAnywhereCondition(int compareTo, Comparison comparison, ConditionFactory.Instance<CachedBlockPosition> blockCondition) {
		this.compareTo = compareTo;
		this.comparison = comparison;
		this.blockCondition = blockCondition;
	}

	@Override
	public boolean test(LivingEntity entity) {
		int stopAt = comparison.getOptimalStoppingIndex(compareTo);
		int count = 0;
		Box box = entity.getBoundingBox();
		BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D);
		BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, Math.min(box.maxY - 0.001D, entity.world.getHeight()), box.maxZ - 0.001D);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		for (int i = blockPos.getX(); i <= blockPos2.getX() && count < stopAt; ++i) {
			for (int j = blockPos.getY(); j <= blockPos2.getY() && count < stopAt; ++j) {
				for (int k = blockPos.getZ(); k <= blockPos2.getZ() && count < stopAt; ++k) {
					mutable.set(i, j, k);
					if (blockCondition.test(new CachedBlockPosition(entity.world, mutable, false))) {
						count++;
					}
				}
			}
		}
		return comparison.compare(count, compareTo);
	}
}
