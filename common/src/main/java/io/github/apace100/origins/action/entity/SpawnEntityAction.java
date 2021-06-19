package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.action.configuration.SpawnEntityConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.registry.Registry;

public class SpawnEntityAction extends EntityAction<SpawnEntityConfiguration> {

	public SpawnEntityAction() {
		super(SpawnEntityConfiguration.CODEC);
	}

	@Override
	public void execute(SpawnEntityConfiguration configuration, Entity entity) {
		if (configuration.type() == null)
			return;
		Entity newEntity = configuration.type().create(entity.getEntityWorld());
		if (newEntity == null) {
			Origins.LOGGER.error("Failed to create entity for type: {}", Registry.ENTITY_TYPE.getId(configuration.type()));
			return;
		}
		if (configuration.tag() != null) {
			CompoundTag tag = newEntity.toTag(new CompoundTag());
			tag.copyFrom(configuration.tag());
			newEntity.fromTag(tag);
		}
		entity.getEntityWorld().spawnEntity(newEntity);
		ConfiguredEntityAction.execute(configuration.action(), newEntity);
	}
}
