package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.ListConfiguration;
import io.github.apace100.origins.util.PositionedItemStack;

public record StartingInventoryConfiguration(ListConfiguration<PositionedItemStack> stacks, boolean recurrent) implements IOriginsFeatureConfiguration {
	public static final Codec<StartingInventoryConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.optionalMapCodec(PositionedItemStack.CODEC, "stack", "stacks").forGetter(StartingInventoryConfiguration::stacks),
			Codec.BOOL.optionalFieldOf("recurrent", false).forGetter(StartingInventoryConfiguration::recurrent)
	).apply(instance, StartingInventoryConfiguration::new));
}
