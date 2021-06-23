package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.configuration.ListConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import io.github.apace100.origins.api.power.configuration.power.IValueModifyingPowerConfiguration;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ModifyBreakSpeedConfiguration(ListConfiguration<EntityAttributeModifier> modifiers,
											@Nullable ConfiguredBlockCondition<?, ?> condition) implements IValueModifyingPowerConfiguration {
	public static final Codec<ModifyBreakSpeedConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.MODIFIER_CODEC.forGetter(ModifyBreakSpeedConfiguration::modifiers),
			ConfiguredBlockCondition.CODEC.optionalFieldOf("block_condition").forGetter(x -> Optional.ofNullable(x.condition()))
	).apply(instance, (t1, t2) -> new ModifyBreakSpeedConfiguration(t1, t2.orElse(null))));
}
