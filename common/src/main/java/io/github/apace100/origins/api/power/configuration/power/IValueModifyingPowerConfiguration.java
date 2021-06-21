package io.github.apace100.origins.api.power.configuration.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.ListConfiguration;
import net.minecraft.entity.attribute.EntityAttributeModifier;

public interface IValueModifyingPowerConfiguration extends IOriginsFeatureConfiguration {
	ListConfiguration<EntityAttributeModifier> modifiers();
}
