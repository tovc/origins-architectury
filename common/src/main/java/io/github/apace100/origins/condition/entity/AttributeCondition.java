package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;

import java.util.Optional;
import java.util.function.Predicate;

public class AttributeCondition implements Predicate<LivingEntity> {
	public static Codec<AttributeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(x -> x.comparison),
			Codec.DOUBLE.fieldOf("compare_to").forGetter(x -> x.compareTo),
			OriginsCodecs.OPTIONAL_ATTRIBUTE.fieldOf("attribute").forGetter(x -> x.attribute)
	).apply(instance, AttributeCondition::new));

	private final Comparison comparison;
	private final double compareTo;
	private final Optional<EntityAttribute> attribute;

	public AttributeCondition(Comparison comparison, double compareTo, Optional<EntityAttribute> attribute) {
		this.comparison = comparison;
		this.compareTo = compareTo;
		this.attribute = attribute;
	}

	@Override
	public boolean test(LivingEntity t) {
		if (!attribute.isPresent())
			return false;
		EntityAttributeInstance attributeInstance = t.getAttributeInstance(attribute.get());
		return comparison.compare(attributeInstance != null ? attributeInstance.getValue() : 0, this.compareTo);
	}
}

