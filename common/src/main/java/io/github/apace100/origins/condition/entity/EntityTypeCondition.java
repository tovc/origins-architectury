package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class EntityTypeCondition implements Predicate<LivingEntity> {

	public static final Codec<EntityTypeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_ENTITY_TYPE.fieldOf("entity_type").forGetter(x -> x.type)
	).apply(instance, EntityTypeCondition::new));

	private final Optional<EntityType<?>> type;

	public EntityTypeCondition(Optional<EntityType<?>> type) {this.type = type;}

	@Override
	public boolean test(LivingEntity entity) {
		return type.map(x -> Objects.equals(x, entity.getType())).orElse(false);
	}
}
