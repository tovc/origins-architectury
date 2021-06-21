package io.github.apace100.origins.registry.condition;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.configuration.ConfiguredDamageCondition;
import io.github.apace100.origins.api.power.factory.DamageCondition;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import io.github.apace100.origins.condition.damage.*;
import io.github.apace100.origins.factory.MetaFactories;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.entity.damage.DamageSource;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class ModDamageConditions {
	public static final BiPredicate<ConfiguredDamageCondition<?, ?>, Pair<DamageSource, Float>> PREDICATE = (config, pair) -> config.check(pair.getLeft(), pair.getRight());

	public static final RegistrySupplier<AmountCondition> AMOUNT = register("amount", AmountCondition::new);
	public static final RegistrySupplier<NameCondition> NAME = register("name", NameCondition::new);
	public static final RegistrySupplier<FireDamageCondition> FIRE = register("fire", FireDamageCondition::new);
	public static final RegistrySupplier<ProjectileCondition> PROJECTILE = register("projectile", ProjectileCondition::new);
	public static final RegistrySupplier<AttackerCondition> ATTACKER = register("attacker", AttackerCondition::new);

	public static void register() {
		MetaFactories.defineMetaConditions(OriginsRegistries.DAMAGE_CONDITION, DelegatedDamageCondition::new, ConfiguredDamageCondition.CODEC, PREDICATE);
	}

	@SuppressWarnings("unchecked")
	private static <T extends DamageCondition<?>> RegistrySupplier<T> register(String name, Supplier<T> factory) {
		return (RegistrySupplier<T>) OriginsRegistries.DAMAGE_CONDITION.registerSupplied(Origins.identifier(name), factory::get);
	}
}
