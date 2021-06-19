package io.github.apace100.origins.condition.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.function.Predicate;

public class AdjacentCondition implements Predicate<CachedBlockPosition> {

	public static final Codec<AdjacentCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(x -> x.comparison),
			Codec.INT.fieldOf("compare_to").forGetter(x -> x.compareTo),
			OriginsCodecs.BLOCK_CONDITION.fieldOf("adjacent_condition").forGetter(x -> x.condition)
	).apply(instance, AdjacentCondition::new));

	private final Comparison comparison;
	private final int compareTo;
	private final ConditionFactory.Instance<CachedBlockPosition> condition;

	public AdjacentCondition(Comparison comparison, int compareTo, ConditionFactory.Instance<CachedBlockPosition> condition) {
		this.comparison = comparison;
		this.compareTo = compareTo;
		this.condition = condition;
	}

	@Override
	public boolean test(CachedBlockPosition pos) {
		int count = Math.toIntExact(Arrays.stream(Direction.values())
				.map(x -> new CachedBlockPosition(pos.getWorld(), pos.getBlockPos().offset(x), true))
				.filter(this.condition).count());
		return comparison.compare(count, compareTo);
	}
}
