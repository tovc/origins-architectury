package io.github.apace100.origins.power.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;

import java.util.function.Predicate;

public class OnBlockCondition implements Predicate<LivingEntity> {

	public static final Codec<OnBlockCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.BLOCK_CONDITION.fieldOf("block_condition").forGetter(x -> x.blockCondition)
	).apply(instance, OnBlockCondition::new));

	private final ConditionFactory.Instance<CachedBlockPosition> blockCondition;

	public OnBlockCondition(ConditionFactory.Instance<CachedBlockPosition> blockCondition) {this.blockCondition = blockCondition;}

	@Override
	public boolean test(LivingEntity entity) {
		return entity.isOnGround() && blockCondition.test(new CachedBlockPosition(entity.world, entity.getBlockPos(), true));
	}
}
