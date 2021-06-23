package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record PreventSleepConfiguration(@Nullable ConfiguredBlockCondition<?, ?> condition, String message,
										boolean allowSpawn) implements IOriginsFeatureConfiguration {
	public static final Codec<PreventSleepConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredBlockCondition.CODEC.optionalFieldOf("block_condition").forGetter(x -> Optional.ofNullable(x.condition())),
			Codec.STRING.optionalFieldOf("message", "origins.cant_sleep").forGetter(PreventSleepConfiguration::message),
			Codec.BOOL.optionalFieldOf("set_spawn_point", false).forGetter(PreventSleepConfiguration::allowSpawn)
	).apply(instance, (t1, t2, t3) -> new PreventSleepConfiguration(t1.orElse(null), t2, t3)));
}
