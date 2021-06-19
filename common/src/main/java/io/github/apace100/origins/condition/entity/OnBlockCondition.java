package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;

import java.util.Optional;
import java.util.function.Predicate;

public class OnBlockCondition implements Predicate<LivingEntity> {

	public static final Codec<OnBlockCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.BLOCK_CONDITION.optionalFieldOf("block_condition").forGetter(x -> x.blockCondition)
	).apply(instance, OnBlockCondition::new));

	private final Optional<ConditionFactory.Instance<CachedBlockPosition>> blockCondition;

	public OnBlockCondition(Optional<ConditionFactory.Instance<CachedBlockPosition>> blockCondition) {this.blockCondition = blockCondition;}

	@Override
	public boolean test(LivingEntity entity) {
		return entity.isOnGround() && blockCondition.map(x -> x.test(new CachedBlockPosition(entity.world, entity.getBlockPos(), true))).orElse(true);
	}
}
