package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.MapCodec;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import net.minecraft.entity.LivingEntity;

import java.util.function.BiPredicate;

public class SingleFieldEntityCondition<T> extends EntityCondition<FieldConfiguration<T>> {
	private final BiPredicate<LivingEntity, T> predicate;

	public SingleFieldEntityCondition(MapCodec<T> codec, BiPredicate<LivingEntity, T> predicate) {
		super(FieldConfiguration.codec(codec));
		this.predicate = predicate;
	}

	@Override
	public boolean check(FieldConfiguration<T> configuration, LivingEntity entity) {
		return this.predicate.test(entity, configuration.value());
	}
}
