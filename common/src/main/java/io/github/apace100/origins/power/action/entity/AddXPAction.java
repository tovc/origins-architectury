package io.github.apace100.origins.power.action.entity;

import io.github.apace100.origins.power.configuration.ExperienceConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class AddXPAction extends EntityAction<ExperienceConfiguration> {

	public AddXPAction() {
		super(ExperienceConfiguration.CODEC);
	}

	@Override
	public void execute(ExperienceConfiguration configuration, Entity entity) {

		if (entity instanceof PlayerEntity) {
			if (configuration.points() > 0)
				((PlayerEntity) entity).addExperience(configuration.points());
			((PlayerEntity) entity).addExperienceLevels(configuration.levels());
		}
	}
}
