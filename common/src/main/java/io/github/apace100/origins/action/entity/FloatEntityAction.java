package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.BiConsumer;

public class FloatEntityAction extends EntityAction<FieldConfiguration<Float>> {

	public static FloatEntityAction ofLiving(BiConsumer<LivingEntity, Float> action, String field) {
		return new FloatEntityAction((e, i) -> {if (e instanceof LivingEntity le) action.accept(le, i);}, field);
	}

	public static FloatEntityAction ofPlayer(BiConsumer<PlayerEntity, Float> action, String field) {
		return new FloatEntityAction((e, i) -> {if (e instanceof PlayerEntity le) action.accept(le, i);}, field);
	}

	private final BiConsumer<Entity, Float> action;

	public FloatEntityAction(BiConsumer<Entity, Float> action, String field) {
		super(FieldConfiguration.codec(FLOAT, field));
		this.action = action;
	}

	@Override
	public void execute(FieldConfiguration<Float> configuration, Entity entity) {
		this.action.accept(entity, configuration.value());
	}
}
