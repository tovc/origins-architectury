package io.github.apace100.origins.condition.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.world.LightType;

import java.util.Optional;
import java.util.function.Predicate;

public class LightLevelCondition implements Predicate<CachedBlockPosition> {
	public static final Codec<LightLevelCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(x -> x.comparison),
			Codec.INT.fieldOf("compare_to").forGetter(x -> x.compareTo),
			OriginsCodecs.LIGHT_TYPE.optionalFieldOf("light_type").forGetter(x -> x.type)
	).apply(instance, LightLevelCondition::new));

	private final Comparison comparison;
	private final Optional<LightType> type;
	private final int compareTo;

	public LightLevelCondition(Comparison comparison, int compareTo, Optional<LightType> type) {
		this.comparison = comparison;
		this.compareTo = compareTo;
		this.type = type;
	}

	@Override
	public boolean test(CachedBlockPosition pos) {
		int value = this.type.map(x -> pos.getWorld().getLightLevel(x, pos.getBlockPos())).orElseGet(() -> pos.getWorld().getLightLevel(pos.getBlockPos()));
		return comparison.compare(value, compareTo);
	}
}
