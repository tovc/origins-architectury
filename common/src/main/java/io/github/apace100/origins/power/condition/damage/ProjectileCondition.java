package io.github.apace100.origins.power.condition.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;

import java.util.Optional;

public class ProjectileCondition implements DamageCondition {

	public static final Codec<ProjectileCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_ENTITY_TYPE.optionalFieldOf("projectile", Optional.empty()).forGetter(x -> x.projectile)
	).apply(instance, ProjectileCondition::new));

	private final Optional<EntityType<?>> projectile;

	public ProjectileCondition(Optional<EntityType<?>> projectile) {this.projectile = projectile;}

	@Override
	public boolean test(DamageSource source, float f) {
		if (source instanceof ProjectileDamageSource) {
			Entity projectile = source.getSource();
			return projectile != null && this.projectile.map(projectile.getType()::equals).orElse(true);
		}
		return false;
	}
}
