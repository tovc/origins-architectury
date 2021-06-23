package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;

import java.util.Optional;

public class OnBlockCondition extends EntityCondition<FieldConfiguration<Optional<ConfiguredBlockCondition<?, ?>>>> {

	public OnBlockCondition() {
		super(FieldConfiguration.optionalCodec(ConfiguredBlockCondition.CODEC, "block_condition"));
	}

	@Override
	public boolean check(FieldConfiguration<Optional<ConfiguredBlockCondition<?, ?>>> configuration, LivingEntity entity) {
		return entity.isOnGround() && configuration.value().map(x -> x.check(new CachedBlockPosition(entity.world, entity.getBlockPos(), true))).orElse(true);
	}
}
