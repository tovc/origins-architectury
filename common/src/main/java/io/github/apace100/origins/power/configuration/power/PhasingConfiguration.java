package io.github.apace100.origins.power.configuration.power;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record PhasingConfiguration(@Nullable ConfiguredBlockCondition<?, ?> phaseCondition, boolean blacklist,
								   RenderType renderType, float viewDistance,
								   @Nullable ConfiguredEntityCondition<?, ?> phaseDownCondition) implements IOriginsFeatureConfiguration {
	public static final Codec<PhasingConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ConfiguredBlockCondition.CODEC.optionalFieldOf("block_condition").forGetter(x -> Optional.ofNullable(x.phaseCondition())),
			Codec.BOOL.optionalFieldOf("blacklist", false).forGetter(PhasingConfiguration::blacklist),
			OriginsCodecs.enumCodec(RenderType.values(), ImmutableMap.of()).optionalFieldOf("render_type", RenderType.BLINDNESS).forGetter(PhasingConfiguration::renderType),
			Codec.FLOAT.optionalFieldOf("view_distance", 10F).forGetter(PhasingConfiguration::viewDistance),
			ConfiguredEntityCondition.CODEC.optionalFieldOf("phase_down_condition").forGetter(x -> Optional.ofNullable(x.phaseDownCondition()))
	).apply(instance, (t1, t2, t3, t4, t5) -> new PhasingConfiguration(t1.orElse(null), t2, t3, t4, t5.orElse(null))));

	public boolean canPhaseDown(LivingEntity entity) {
		return this.phaseDownCondition() == null ? entity.isSneaking() : this.phaseDownCondition().check(entity);
	}

	public boolean canPhaseThrough(CachedBlockPosition cbp) {
		return this.blacklist() != ConfiguredBlockCondition.check(this.phaseCondition(), cbp);
	}

	public enum RenderType {
		BLINDNESS, REMOVE_BLOCKS;
	}
}
