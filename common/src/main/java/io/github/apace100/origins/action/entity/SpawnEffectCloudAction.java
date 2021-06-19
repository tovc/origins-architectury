package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.action.configuration.SpawnEffectCloudConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

public class SpawnEffectCloudAction extends EntityAction<SpawnEffectCloudConfiguration> {

	public SpawnEffectCloudAction() {
		super(SpawnEffectCloudConfiguration.CODEC);
	}

	@Override
	public void execute(SpawnEffectCloudConfiguration configuration, Entity entity) {
		AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(entity.world, entity.getX(), entity.getY(), entity.getZ());
		if (entity instanceof LivingEntity)
			areaEffectCloudEntity.setOwner((LivingEntity) entity);
		areaEffectCloudEntity.setRadius(configuration.radius());
		areaEffectCloudEntity.setRadiusOnUse(configuration.radiusOnUse());
		areaEffectCloudEntity.setWaitTime(configuration.waitTime());
		areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float) areaEffectCloudEntity.getDuration());
		configuration.effects().getContent().stream().map(StatusEffectInstance::new).forEach(areaEffectCloudEntity::addEffect);
		entity.world.spawnEntity(areaEffectCloudEntity);
	}
}
