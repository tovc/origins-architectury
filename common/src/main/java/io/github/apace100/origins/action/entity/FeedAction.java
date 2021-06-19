package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.action.configuration.FoodConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class FeedAction extends EntityAction<FoodConfiguration> {

	public FeedAction() {
		super(FoodConfiguration.CODEC);
	}

	@Override
	public void execute(FoodConfiguration configuration, Entity entity) {
		if (entity instanceof PlayerEntity player)
			player.getHungerManager().add(configuration.food(), configuration.saturation());
	}
}
