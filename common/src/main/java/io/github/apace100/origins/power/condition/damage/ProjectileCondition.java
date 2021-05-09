package io.github.apace100.origins.power.condition.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class ProjectileCondition implements DamageCondition{

	public static final Codec<ProjectileCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Registry.ENTITY_TYPE.optionalFieldOf("projectile").forGetter(x -> x.projectile)
	).apply(instance, ProjectileCondition::new));

	private final Optional<EntityType<?>> projectile;

	public ProjectileCondition(Optional<EntityType<?>> projectile) {this.projectile = projectile;}

	@Override
	public boolean test(DamageSource source, float f) {
		if(source instanceof ProjectileDamageSource) {
			Entity projectile = source.getSource();
			return projectile != null && this.projectile.map(projectile.getType()::equals).orElse(true);
		}
		return false;
	}
}
