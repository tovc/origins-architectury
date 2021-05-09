package io.github.apace100.origins.power.factory.condition;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.MetaFactories;
import io.github.apace100.origins.power.condition.damage.*;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.function.Predicate;

public class DamageConditions {

    public static void register() {
        MetaFactories.defineMetaConditions(ModRegistriesArchitectury.DAMAGE_CONDITION, OriginsCodecs.DAMAGE_CONDITION);
        register("amount", AmountCondition.CODEC);
        register("fire", DamageTypeCondition.codec(DamageSource::isFire));
        register("name", NameCondition.CODEC);
        register("projectile", ProjectileCondition.CODEC);
        register("attacker", AttackerCondition.CODEC);
    }

    private static void register(String name, Codec<? extends Predicate<Pair<DamageSource, Float>>> codec) {
        ModRegistriesArchitectury.DAMAGE_CONDITION.registerSupplied(Origins.identifier(name), () -> new ConditionFactory<>(codec));
    }
}
