package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.condition.configuration.OriginConfiguration;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.registry.Registry;

public class OriginCondition extends EntityCondition<OriginConfiguration> {

	public OriginCondition() {
		super(OriginConfiguration.CODEC.codec());
	}

	@Override
	public boolean check(OriginConfiguration configuration, LivingEntity entity) {
		OriginComponent component = ModComponentsArchitectury.getOriginComponent(entity);
		Registry<Origin> origins = OriginsAPI.getOrigins();
		Registry<OriginLayer> layers = OriginsAPI.getLayers();
		if (configuration.layer() != null)
			return layers.getOrEmpty(configuration.layer()).map(component::getOrigin).map(origins::getId).map(configuration.origin()::equals).orElse(false);
		return component.getOrigins().values().stream().anyMatch(configuration.origin()::equals);
	}
}
