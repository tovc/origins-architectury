package io.github.apace100.origins.util;

import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.util.Comparator;
import java.util.List;

public final class AttributeUtil {

    @Deprecated
    public static void sortModifiers(List<EntityAttributeModifier> modifiers) {
        modifiers.sort(Comparator.comparing(e -> e.getOperation().getId()));
    }

    /**
     * @deprecated Use {@link #applyModifiers(List, double)} instead.
     * That version doesn't have the sorting overhead.
     */
    @Deprecated
    public static double sortAndApplyModifiers(List<EntityAttributeModifier> modifiers, double baseValue) {
        sortModifiers(modifiers);
        return applyModifiers(modifiers, baseValue);
    }

    public static double applyModifiers(List<EntityAttributeModifier> modifiers, double baseValue) {
        double value = baseValue;
        double multiplier = 1.0F;
        for (EntityAttributeModifier modifier : modifiers) {
            switch (modifier.getOperation()) {
                case ADDITION -> value += modifier.getValue();
                case MULTIPLY_BASE -> value += modifier.getValue() * baseValue;
                case MULTIPLY_TOTAL -> multiplier *= modifier.getValue();
            }
        }
        return multiplier * value;
    }
}
