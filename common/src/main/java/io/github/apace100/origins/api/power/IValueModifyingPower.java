package io.github.apace100.origins.api.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public interface IValueModifyingPower<T extends IOriginsFeatureConfiguration> {
	List<EntityAttributeModifier> getModifiers(ConfiguredPower<T, ?> configuration, PlayerEntity player);
}
