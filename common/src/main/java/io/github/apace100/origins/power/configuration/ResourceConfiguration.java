package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.configuration.power.IHudRenderedVariableIntPowerConfiguration;
import io.github.apace100.origins.api.power.configuration.power.IVariableIntPowerConfiguration;
import io.github.apace100.origins.util.HudRender;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ResourceConfiguration(HudRender hudRender, int initialValue, int min, int max, @Nullable ConfiguredEntityAction<?, ?> minAction, @Nullable ConfiguredEntityAction<?, ?> maxAction) implements IHudRenderedVariableIntPowerConfiguration {
	public static final Codec<ResourceConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			HudRender.CODEC.fieldOf("hud_render").forGetter(IHudRenderedVariableIntPowerConfiguration::hudRender),
			Codec.INT.optionalFieldOf("start_value").forGetter(x -> x.min() == x.initialValue() ? Optional.empty() : Optional.of(x.initialValue())),
			Codec.INT.fieldOf("min").forGetter(IVariableIntPowerConfiguration::min),
			Codec.INT.fieldOf("max").forGetter(IVariableIntPowerConfiguration::max),
			ConfiguredEntityAction.CODEC.optionalFieldOf("min_action").forGetter(x -> Optional.ofNullable(x.minAction())),
			ConfiguredEntityAction.CODEC.optionalFieldOf("max_action").forGetter(x -> Optional.ofNullable(x.maxAction()))
	).apply(instance, (t1, t2, t3, t4, t5, t6) -> new ResourceConfiguration(t1, t2.orElse(t3), t3, t4, t5.orElse(null), t6.orElse(null))));
}
