package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.ListConfiguration;
import io.github.apace100.origins.util.AttributedEntityAttributeModifier;
import io.github.apace100.origins.util.OriginsCodecs;

public record ConditionedAttributeConfiguration(ListConfiguration<AttributedEntityAttributeModifier> modifiers, int tickRate) implements IOriginsFeatureConfiguration {
	public static final Codec<ConditionedAttributeConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.optionalMapCodec(OriginsCodecs.OPTIONAL_ATTRIBUTED_ATTRIBUTE_MODIFIER, "modifier", "modifiers").forGetter(ConditionedAttributeConfiguration::modifiers),
			Codec.INT.optionalFieldOf("tickRate", 20).forGetter(ConditionedAttributeConfiguration::tickRate)
	).apply(instance, ConditionedAttributeConfiguration::new));
}
