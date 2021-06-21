package io.github.apace100.origins.condition.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.IntegerComparisonConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record LightLevelConfiguration(IntegerComparisonConfiguration comparison,
									  @Nullable LightType type) implements IOriginsFeatureConfiguration {
	public static final Codec<LightLevelConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			IntegerComparisonConfiguration.MAP_CODEC.forGetter(LightLevelConfiguration::comparison),
			OriginsCodecs.LIGHT_TYPE.optionalFieldOf("light_type").forGetter(x -> Optional.ofNullable(x.type()))
	).apply(instance, (comparison, lightType) -> new LightLevelConfiguration(comparison, lightType.orElse(null))));

	public int getLightLevel(WorldView world, BlockPos pos) {
		return this.type() == null ? world.getLightLevel(pos) : world.getLightLevel(this.type(), pos);
	}
}
