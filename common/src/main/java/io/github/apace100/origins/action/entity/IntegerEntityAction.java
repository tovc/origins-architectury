package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.api.power.configuration.power.FieldConfiguration;
import io.github.apace100.origins.api.power.configuration.power.NoConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class IntegerEntityAction extends EntityAction<FieldConfiguration<Integer>> {

	public static IntegerEntityAction ofLiving(BiConsumer<LivingEntity, Integer> action, String field) {
		return new IntegerEntityAction((e, i) -> {if (e instanceof LivingEntity le) action.accept(le, i);}, field);
	}

	public static IntegerEntityAction ofPlayer(BiConsumer<PlayerEntity, Integer> action, String field) {
		return new IntegerEntityAction((e, i) -> {if (e instanceof PlayerEntity le) action.accept(le, i);}, field);
	}

	private final BiConsumer<Entity, Integer> action;

	public IntegerEntityAction(BiConsumer<Entity, Integer> action, String field) {
		super(FieldConfiguration.codec(INT, field));
		this.action = action;
	}

	@Override
	public void execute(FieldConfiguration<Integer> configuration, Entity entity) {
		this.action.accept(entity, configuration.value());
	}
}
