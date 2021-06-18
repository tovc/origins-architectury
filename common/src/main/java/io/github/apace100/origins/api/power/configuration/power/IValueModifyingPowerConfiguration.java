package io.github.apace100.origins.api.power.configuration.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.power.configuration.ListConfiguration;
import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.util.List;

public interface IValueModifyingPowerConfiguration extends IOriginsFeatureConfiguration {
	ListConfiguration<EntityAttributeModifier> modifiers();
}
