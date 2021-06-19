package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.api.power.configuration.power.NoConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Consumer;

public class SimpleEntityAction extends EntityAction<NoConfiguration> {

	public static SimpleEntityAction ofLiving(Consumer<LivingEntity> action) {
		return new SimpleEntityAction((Entity e) -> {if (e instanceof LivingEntity le) action.accept(le);});
	}

	public static SimpleEntityAction ofPlayer(Consumer<PlayerEntity> action) {
		return new SimpleEntityAction((Entity e) -> {if (e instanceof PlayerEntity le) action.accept(le);});
	}

	private final Consumer<Entity> action;

	public SimpleEntityAction(Consumer<Entity> action) {
		super(NoConfiguration.CODEC);
		this.action = action;
	}

	@Override
	public void execute(NoConfiguration configuration, Entity entity) {
		this.action.accept(entity);
	}
}
