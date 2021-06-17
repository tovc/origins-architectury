package io.github.apace100.origins.registry;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import io.github.apace100.origins.power.factories.EntityGlowPower;
import io.github.apace100.origins.power.factories.ElytraFlightPower;
import io.github.apace100.origins.power.factories.*;
import me.shedaniel.architectury.registry.RegistrySupplier;

import java.util.function.Supplier;

public class ModPowers {
	public static void initialize() { }

	public static final RegistrySupplier<ActionOnBlockBreakPower> ACTION_ON_BLOCK_BREAK = register("action_on_block_break", ActionOnBlockBreakPower::new);
	public static final RegistrySupplier<ActionOnCallbackPower> ACTION_ON_CALLBACK = register("action_on_callback", ActionOnCallbackPower::new);
	public static final RegistrySupplier<ActionOnItemUsePower> ACTION_ON_ITEM_USE = register("action_on_item_use", ActionOnItemUsePower::new);
	public static final RegistrySupplier<ActionOnLandPower> ACTION_ON_LAND = register("action_on_land", ActionOnLandPower::new);
	public static final RegistrySupplier<ActionOnWakeUpPower> ACTION_ON_WAKE_UP = register("action_on_wake_up", ActionOnWakeUpPower::new);
	public static final RegistrySupplier<ActionOverTimePower> ACTION_OVER_TIME = register("action_over_time", ActionOverTimePower::new);
	public static final RegistrySupplier<ActiveSelfPower> ACTIVE_SELF = register("active_self", ActiveSelfPower::new);
	public static final RegistrySupplier<AttackerActionWhenHitPower> ATTACKER_ACTION_WHEN_HIT = register("attacker_action_when_hit", AttackerActionWhenHitPower::new);
	public static final RegistrySupplier<AttributePower> ATTRIBUTE = register("attribute", AttributePower::new);
	public static final RegistrySupplier<BurnPower> BURN = register("burn", BurnPower::new);
	public static final RegistrySupplier<ClimbingPower> CLIMBING = register("climbing", ClimbingPower::new);
	public static final RegistrySupplier<ConditionedAttributePower> CONDITIONED_ATTRIBUTE = register("conditioned_attribute", ConditionedAttributePower::new);
	public static final RegistrySupplier<ConditionedRestrictArmorPower> CONDITIONED_RESTRICT_ARMOR = register("conditioned_restrict_armor", ConditionedRestrictArmorPower::new);
	public static final RegistrySupplier<CooldownPower> COOLDOWN = register("cooldown", CooldownPower::new);
	public static final RegistrySupplier<DamageOverTimePower> DAMAGE_OVER_TIME = register("damage_over_time", DamageOverTimePower::new);
	public static final RegistrySupplier<DummyPower> DISABLE_REGEN = register("disable_regen", DummyPower::new);
	public static final RegistrySupplier<EffectImmunityPower> EFFECT_IMMUNITY = register("effect_immunity", EffectImmunityPower::new);
	public static final RegistrySupplier<ElytraFlightPower> ELYTRA_FLIGHT = register("elytra_flight", ElytraFlightPower::new);
	public static final RegistrySupplier<EntityGlowPower> ENTITY_GLOW = register("entity_glow", EntityGlowPower::new);
	public static final RegistrySupplier<DummyPower> FIRE_IMMUNITY = register("fire_immunity", DummyPower::new);
	public static final RegistrySupplier<DummyPower> SWIMMING = register("swimming", DummyPower::new);
	public static final RegistrySupplier<DummyPower> SIMPLE = register("simple", DummyPower::new);
	public static final RegistrySupplier<DummyPower> SHAKING = register("shaking", DummyPower::new);
	public static final RegistrySupplier<DummyPower> IGNORE_WATER = register("ignore_water", DummyPower::new);

	public static final RegistrySupplier<RestrictArmorPower> RESTRICT_ARMOR = register("restrict_armor", RestrictArmorPower::new);
	public static final RegistrySupplier<SelfActionWhenHitPower> SELF_ACTION_WHEN_HIT = register("self_action_when_hit", SelfActionWhenHitPower::new);

	@SuppressWarnings("unchecked")
	private static <T extends PowerFactory<?>> RegistrySupplier<T> register(String name, Supplier<T> factory) {
		return (RegistrySupplier<T>) OriginsRegistries.POWER_FACTORY.registerSupplied(Origins.identifier(name), factory::get);
	}
}
