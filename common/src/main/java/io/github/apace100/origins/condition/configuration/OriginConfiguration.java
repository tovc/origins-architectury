package io.github.apace100.origins.condition.configuration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record OriginConfiguration(Identifier origin,
								  @Nullable Identifier layer) implements IOriginsFeatureConfiguration {
	public static final MapCodec<OriginConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Identifier.CODEC.fieldOf("origin").forGetter(OriginConfiguration::origin),
			Identifier.CODEC.optionalFieldOf("layer").forGetter(x -> Optional.ofNullable(x.layer()))
	).apply(instance, (t1, t2) -> new OriginConfiguration(t1, t2.orElse(null))));
}
