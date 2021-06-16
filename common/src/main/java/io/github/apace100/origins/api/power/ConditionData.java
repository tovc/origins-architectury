package io.github.apace100.origins.api.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Information common to all condition types.<br/>
 * Using this class is safe, that is no methods or field will
 * be removed from this class, and those will only be added.
 */
public record ConditionData(boolean inverted) {
	public static final ConditionData DEFAULT = new ConditionData(false);

	public static final MapCodec<ConditionData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("inverted", false).forGetter(ConditionData::inverted)
	).apply(instance, ConditionData::new));
}
