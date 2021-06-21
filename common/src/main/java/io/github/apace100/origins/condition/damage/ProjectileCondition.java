package io.github.apace100.origins.condition.damage;

import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.DamageCondition;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;

import java.util.Optional;

public class ProjectileCondition extends DamageCondition<FieldConfiguration<Optional<EntityType<?>>>> {

	public ProjectileCondition() {
		super(FieldConfiguration.codec(OriginsCodecs.OPTIONAL_ENTITY_TYPE, "projectile", Optional.empty()));
	}

	@Override
	protected boolean check(FieldConfiguration<Optional<EntityType<?>>> configuration, DamageSource source, float amount) {
		if (source instanceof ProjectileDamageSource) {
			Entity projectile = source.getSource();
			return projectile != null && configuration.value().map(projectile.getType()::equals).orElse(true);
		}
		return false;
	}
}
