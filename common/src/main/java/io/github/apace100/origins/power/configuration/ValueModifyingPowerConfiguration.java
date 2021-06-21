package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.configuration.power.IValueModifyingPowerConfiguration;
import io.github.apace100.origins.api.configuration.ListConfiguration;
import net.minecraft.entity.attribute.EntityAttributeModifier;

public record ValueModifyingPowerConfiguration(ListConfiguration<EntityAttributeModifier> modifiers) implements IValueModifyingPowerConfiguration {
	public static Codec<ValueModifyingPowerConfiguration> CODEC = ListConfiguration.MODIFIER_CODEC
			.xmap(ValueModifyingPowerConfiguration::new, ValueModifyingPowerConfiguration::modifiers).codec();

	@Override
	public boolean isConfigurationValid() {
		return !this.modifiers().getContent().isEmpty();
	}
}
