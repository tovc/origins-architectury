package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;

import java.util.Objects;
import java.util.function.Predicate;

public class EntityGroupCondition implements Predicate<LivingEntity> {

	public static final Codec<EntityGroupCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.ENTITY_GROUP.fieldOf("group").forGetter(x -> x.group)
	).apply(instance, EntityGroupCondition::new));

	private final EntityGroup group;

	public EntityGroupCondition(EntityGroup group) {this.group = group;}

	@Override
	public boolean test(LivingEntity entity) {
		return Objects.equals(entity.getGroup(), this.group);
	}
}
