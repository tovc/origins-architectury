package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class OriginCondition implements Predicate<LivingEntity> {
	public static final Codec<OriginCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("origin").forGetter(x -> x.origin),
			Identifier.CODEC.optionalFieldOf("layer").forGetter(x -> x.layer)
	).apply(instance, OriginCondition::new));

	private final Identifier origin;
	private final Optional<Identifier> layer;

	public OriginCondition(Identifier origin, Optional<Identifier> layer) {
		this.origin = origin;
		this.layer = layer;
	}

	@Override
	public boolean test(LivingEntity entity) {
		OriginComponent component = ModComponentsArchitectury.getOriginComponent(entity);
		if (layer.isPresent())
			return layer.map(OriginLayers::getLayer)
					.map(component::getOrigin)
					.map(x -> Objects.equals(x.getIdentifier(), this.origin))
					.orElse(false);
		return component.getOrigins().values().stream().anyMatch(o -> o.getIdentifier().equals(origin));
	}
}
