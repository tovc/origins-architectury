package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.condition.configuration.CommandComparisonConfiguration;
import net.minecraft.entity.LivingEntity;

import java.util.OptionalInt;

public class CommandCondition extends EntityCondition<CommandComparisonConfiguration> {

	public CommandCondition() {
		super(CommandComparisonConfiguration.CODEC);
	}

	@Override
	public boolean check(CommandComparisonConfiguration configuration, LivingEntity entity) {
		OptionalInt execute = configuration.command().execute(entity);
		return execute.isPresent() && configuration.comparison().check(execute.getAsInt());
	}
}
