package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.Optional;

public class PreventEntityRenderPower extends PowerFactory<FieldConfiguration<Optional<ConfiguredEntityCondition<?, ?>>>> {

	public static boolean isRenderPrevented(Entity entity, Entity target) {
		return OriginComponent.getPowers(entity, ModPowers.PREVENT_ENTITY_RENDER.get()).stream().anyMatch(x -> x.getFactory().doesPrevent(x, target));
	}

	public PreventEntityRenderPower() {
		super(FieldConfiguration.optionalCodec(ConfiguredEntityCondition.CODEC, "entity_condition"));
	}

	public boolean doesPrevent(ConfiguredPower<FieldConfiguration<Optional<ConfiguredEntityCondition<?, ?>>>, ?> configuration, Entity e) {
		return e instanceof LivingEntity le && configuration.getConfiguration().value().map(x -> x.check(le)).orElse(true);
	}
}
