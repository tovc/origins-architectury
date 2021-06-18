package io.github.apace100.origins.api.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.api.power.factory.power.ValueModifyingPowerFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public interface IValueModifyingPower<T extends IOriginsFeatureConfiguration> {
	List<EntityAttributeModifier> getModifiers(ConfiguredPower<T, ?> configuration, PlayerEntity player);
}
