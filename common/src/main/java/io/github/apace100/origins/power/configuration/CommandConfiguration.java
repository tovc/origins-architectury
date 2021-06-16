package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

public record CommandConfiguration(String command,
								   int permissionLevel) implements IOriginsFeatureConfiguration {
	public static final Codec<CommandConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("command").forGetter(CommandConfiguration::command),
			Codec.INT.optionalFieldOf("permission_level", 4).forGetter(CommandConfiguration::permissionLevel)
	).apply(instance, CommandConfiguration::new));
}
