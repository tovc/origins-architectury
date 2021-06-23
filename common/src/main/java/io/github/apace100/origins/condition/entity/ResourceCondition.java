package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.condition.configuration.ResourceComparisonConfiguration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.OptionalInt;

public class ResourceCondition extends EntityCondition<ResourceComparisonConfiguration> {

	public ResourceCondition() {
		super(ResourceComparisonConfiguration.CODEC);
	}

	@Override
	public boolean check(ResourceComparisonConfiguration configuration, LivingEntity entity) {
		OriginComponent component = OriginsAPI.getComponent(entity);
		ConfiguredPower<?, ?> power = component.getPower(configuration.resource().power());
		if (entity instanceof PlayerEntity player) {
			OptionalInt value = power.getValue(player);
			return value.isPresent() && configuration.comparison().check(value.getAsInt());
		}
		return false;
	}
}
