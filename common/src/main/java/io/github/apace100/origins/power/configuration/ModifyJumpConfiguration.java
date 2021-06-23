package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.configuration.ListConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.configuration.power.IValueModifyingPowerConfiguration;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ModifyJumpConfiguration(ListConfiguration<EntityAttributeModifier> modifiers,
									  @Nullable ConfiguredEntityAction<?, ?> condition) implements IValueModifyingPowerConfiguration {
	public static final Codec<ModifyJumpConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ListConfiguration.MODIFIER_CODEC.forGetter(ModifyJumpConfiguration::modifiers),
			ConfiguredEntityAction.CODEC.optionalFieldOf("entity_action").forGetter(x -> Optional.ofNullable(x.condition()))
	).apply(instance, (t1, t2) -> new ModifyJumpConfiguration(t1, t2.orElse(null))));
}
