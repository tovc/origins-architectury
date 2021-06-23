package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

public class EntityGlowPower extends PowerFactory<FieldConfiguration<Optional<ConfiguredEntityCondition<?, ?>>>> {

	public static boolean shouldGlow(PlayerEntity player, Entity entity) {
		return OriginComponent.getPowers(player, ModPowers.ENTITY_GLOW.get()).stream().anyMatch(x -> x.getFactory().doesApply(x, entity));
	}

	public EntityGlowPower() {
		super(FieldConfiguration.optionalCodec(ConfiguredEntityCondition.CODEC, "entity_condition"));
	}

	public boolean doesApply(ConfiguredPower<FieldConfiguration<Optional<ConfiguredEntityCondition<?, ?>>>, ?> configuration, Entity target) {
		return target instanceof LivingEntity livingEntity && configuration.getConfiguration().value().map(x -> x.check(livingEntity)).orElse(true);
	}
}
