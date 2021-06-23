package io.github.apace100.origins.util;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;

public record AttributedEntityAttributeModifier(EntityAttribute attribute,
												EntityAttributeModifier modifier) {
}
