package io.github.apace100.origins.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.DoubleComparisonConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.attribute.EntityAttribute;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record AttributeComparisonConfiguration(@Nullable EntityAttribute attribute,
											   DoubleComparisonConfiguration comparison) implements IOriginsFeatureConfiguration {
	public static Codec<AttributeComparisonConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_ATTRIBUTE.fieldOf("attribute").forGetter(x -> Optional.ofNullable(x.attribute())),
			DoubleComparisonConfiguration.MAP_CODEC.forGetter(AttributeComparisonConfiguration::comparison)
	).apply(instance, (t1, t2) -> new AttributeComparisonConfiguration(t1.orElse(null), t2)));
}
