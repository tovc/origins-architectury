package io.github.apace100.origins.power.condition.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.pattern.CachedBlockPosition;

import java.util.function.Predicate;

public class OffsetCondition implements Predicate<CachedBlockPosition> {

	public static final Codec<OffsetCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.BLOCK_CONDITION.fieldOf("condition").forGetter(x -> x.condition),
			Codec.INT.optionalFieldOf("x", 0).forGetter(x -> x.x),
			Codec.INT.optionalFieldOf("y", 0).forGetter(x -> x.y),
			Codec.INT.optionalFieldOf("z", 0).forGetter(x -> x.z)
	).apply(instance, OffsetCondition::new));

	private final ConditionFactory.Instance<CachedBlockPosition> condition;
	private final int x;
	private final int y;
	private final int z;

	public OffsetCondition(ConditionFactory.Instance<CachedBlockPosition> condition, int x, int y, int z) {
		this.condition = condition;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean test(CachedBlockPosition cachedBlockPosition) {
		return this.condition.test(new CachedBlockPosition(cachedBlockPosition.getWorld(), cachedBlockPosition.getBlockPos().add(x, y, z), true));
	}
}
