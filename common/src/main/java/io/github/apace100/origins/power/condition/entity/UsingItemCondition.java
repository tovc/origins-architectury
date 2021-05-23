package io.github.apace100.origins.power.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

public class UsingItemCondition implements Predicate<LivingEntity> {

	public static final Codec<UsingItemCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.ITEM_CONDITION.optionalFieldOf("item_condition").forGetter(x -> x.itemCondition)
	).apply(instance, UsingItemCondition::new));

	private final Optional<ConditionFactory.Instance<ItemStack>> itemCondition;

	public UsingItemCondition(Optional<ConditionFactory.Instance<ItemStack>> itemCondition) {this.itemCondition = itemCondition;}

	@Override
	public boolean test(LivingEntity entity) {
		return entity.isUsingItem() && this.itemCondition.map(x -> x.test(entity.getStackInHand(entity.getActiveHand()))).orElse(true);
	}
}
