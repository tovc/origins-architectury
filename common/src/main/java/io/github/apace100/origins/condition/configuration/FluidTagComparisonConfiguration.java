package io.github.apace100.origins.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.DoubleComparisonConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;

public record FluidTagComparisonConfiguration(DoubleComparisonConfiguration comparison,
											  Tag<Fluid> tag) implements IOriginsFeatureConfiguration {
	public static Codec<FluidTagComparisonConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DoubleComparisonConfiguration.MAP_CODEC.forGetter(FluidTagComparisonConfiguration::comparison),
			OriginsCodecs.FLUID_TAG.fieldOf("fluid").forGetter(FluidTagComparisonConfiguration::tag)
	).apply(instance, FluidTagComparisonConfiguration::new));
}
