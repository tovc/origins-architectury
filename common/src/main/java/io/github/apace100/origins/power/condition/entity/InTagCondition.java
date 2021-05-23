package io.github.apace100.origins.power.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tag.Tag;

import java.util.function.Predicate;

public class InTagCondition implements Predicate<LivingEntity> {

	public static final Codec<InTagCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.ENTITY_TAG.fieldOf("tag").forGetter(x -> x.tag)
	).apply(instance, InTagCondition::new));

	private final Tag<EntityType<?>> tag;

	public InTagCondition(Tag<EntityType<?>> tag) {this.tag = tag;}

	@Override
	public boolean test(LivingEntity entity) {
		return entity.getType().isIn(this.tag);
	}
}
