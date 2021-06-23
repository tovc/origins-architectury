package io.github.apace100.origins.registry;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import io.github.apace100.origins.power.*;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;

import java.util.function.Supplier;

public class ModPowers {
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
	public static final RegistrySupplier<EntityGroupPower> ENTITY_GROUP = register("entity_group", EntityGroupPower::new);
	public static final RegistrySupplier<ExhaustOverTimePower> EXHAUST_OVER_TIME = register("exhaust", ExhaustOverTimePower::new);
	public static final RegistrySupplier<DummyPower> FIRE_IMMUNITY = register("fire_immunity", DummyPower::new);
	public static final RegistrySupplier<FireProjectilePower> FIRE_PROJECTILE = register("fire_projectile", FireProjectilePower::new);
	public static final RegistrySupplier<DummyPower> IGNORE_WATER = register("ignore_water", DummyPower::new);
	public static final RegistrySupplier<InventoryPower> INVENTORY = register("inventory", () -> new InventoryPower(9, inventory -> (i, playerInv, player) -> new Generic3x3ContainerScreenHandler(i, playerInv, inventory)));
	public static final RegistrySupplier<InvisibilityPower> INVISIBILITY = register("invisibility", InvisibilityPower::new);
	public static final RegistrySupplier<InvulnerablePower> INVULNERABILITY = register("invulnerability", InvulnerablePower::new);
	public static final RegistrySupplier<LaunchPower> LAUNCH = register("launch", LaunchPower::new);
	public static final RegistrySupplier<LavaVisionPower> LAVA_VISION = register("lava_vision", LavaVisionPower::new);
	public static final RegistrySupplier<ModelColorPower> MODEL_COLOR = register("model_color", ModelColorPower::new);
	public static final RegistrySupplier<ModifyBreakSpeedPower> MODIFY_BREAK_SPEED = register("modify_break_speed", ModifyBreakSpeedPower::new);
	public static final RegistrySupplier<ModifyDamageDealtPower> MODIFY_DAMAGE_DEALT = register("modify_damage_dealt", ModifyDamageDealtPower::new);
	public static final RegistrySupplier<ModifyDamageTakenPower> MODIFY_DAMAGE_TAKEN = register("modify_damage_taken", ModifyDamageTakenPower::new);
	public static final RegistrySupplier<ModifyValuePower> MODIFY_EXHAUSTION = register("modify_exhaustion", ModifyValuePower::new);
	public static final RegistrySupplier<ModifyValuePower> MODIFY_EXPERIENCE = register("modify_xp_gain", ModifyValuePower::new);
	public static final RegistrySupplier<ModifyFoodPower> MODIFY_FOOD = register("modify_food", ModifyFoodPower::new);
	public static final RegistrySupplier<ModifyHarvestPower> MODIFY_HARVEST = register("modify_harvest", ModifyHarvestPower::new);
	public static final RegistrySupplier<ModifyJumpPower> MODIFY_JUMP = register("modify_jump", ModifyJumpPower::new);
	public static final RegistrySupplier<ModifyValuePower> MODIFY_LAVA_SPEED = register("modify_lava_speed", ModifyValuePower::new);
	public static final RegistrySupplier<ModifyPlayerSpawnPower> MODIFY_PLAYER_SPAWN = register("modify_player_spawn", ModifyPlayerSpawnPower::new);
	public static final RegistrySupplier<ModifyDamageDealtPower> MODIFY_PROJECTILE_DAMAGE = register("modify_projectile_damage", ModifyDamageDealtPower::new);
	public static final RegistrySupplier<ModifySwimSpeedPower> MODIFY_SWIM_SPEED = register("modify_swim_speed", ModifySwimSpeedPower::new);
	public static final RegistrySupplier<MultiplePower> MULTIPLE = register("multiple", MultiplePower::new);
	public static final RegistrySupplier<NightVisionPower> NIGHT_VISION = register("night_vision", NightVisionPower::new);
	public static final RegistrySupplier<ParticlePower> PARTICLE = register("particle", ParticlePower::new);
	public static final RegistrySupplier<PhasingPower> PHASING = register("phasing", PhasingPower::new);
	public static final RegistrySupplier<PreventBlockActionPower> PREVENT_BLOCK_SELECTION = register("prevent_block_selection", PreventBlockActionPower::new);
	public static final RegistrySupplier<PreventBlockActionPower> PREVENT_BLOCK_USAGE = register("prevent_block_use", PreventBlockActionPower::new);
	public static final RegistrySupplier<PreventDeathPower> PREVENT_DEATH = register("prevent_death", PreventDeathPower::new);
	public static final RegistrySupplier<PreventEntityRenderPower> PREVENT_ENTITY_RENDER = register("prevent_entity_render", PreventEntityRenderPower::new);
	public static final RegistrySupplier<PreventItemActionPower> PREVENT_ITEM_USAGE = register("prevent_item_use", PreventItemActionPower::new);
	public static final RegistrySupplier<PreventSleepPower> PREVENT_SLEEP = register("prevent_sleep", PreventSleepPower::new);
	public static final RegistrySupplier<RecipePower> RECIPE = register("recipe", RecipePower::new);
	public static final RegistrySupplier<ResourcePower> RESOURCE = register("resource", ResourcePower::new);
	public static final RegistrySupplier<RestrictArmorPower> RESTRICT_ARMOR = register("restrict_armor", RestrictArmorPower::new);
	public static final RegistrySupplier<SelfCombatActionPower> SELF_ACTION_ON_HIT = register("self_action_on_hit", SelfCombatActionPower::new);
	public static final RegistrySupplier<SelfCombatActionPower> SELF_ACTION_ON_KILL = register("self_action_on_kill", SelfCombatActionPower::new);
	public static final RegistrySupplier<SelfActionWhenHitPower> SELF_ACTION_WHEN_HIT = register("self_action_when_hit", SelfActionWhenHitPower::new);
	public static final RegistrySupplier<ShaderPower> SHADER = register("shader", ShaderPower::new);
	public static final RegistrySupplier<DummyPower> SHAKING = register("shaking", DummyPower::new);
	public static final RegistrySupplier<DummyPower> SIMPLE = register("simple", DummyPower::new);
	public static final RegistrySupplier<StackingStatusEffectPower> STACKING_STATUS_EFFECT = register("stacking_status_effect", StackingStatusEffectPower::new);
	public static final RegistrySupplier<StartingEquipmentPower> STARTING_EQUIPMENT = register("starting_equipment", StartingEquipmentPower::new);
	public static final RegistrySupplier<DummyPower> SWIMMING = register("swimming", DummyPower::new);
	public static final RegistrySupplier<TargetCombatActionPower> TARGET_ACTION_ON_HIT = register("target_action_on_hit", TargetCombatActionPower::new);
	public static final RegistrySupplier<TogglePower> TOGGLE = register("toggle", TogglePower::new);
	public static final RegistrySupplier<ToggleNightVisionPower> TOGGLE_NIGHT_VISION = register("toggle_night_vision", ToggleNightVisionPower::new);
	public static final RegistrySupplier<WalkOnFluidPower> WALK_ON_FLUID = register("walk_on_fluid", WalkOnFluidPower::new);

	//Those powers are, as far as I know, remains of the previous system.
	//As such, I've transformed then into actual powers.
	public static final RegistrySupplier<CooldownPower> WEBBING = register("webbing", CooldownPower::new);
	public static final RegistrySupplier<DummyPower> WATER_BREATHING = register("water_breathing", DummyPower::new);
	public static final RegistrySupplier<DummyPower> NO_COBWEB_SLOWDOWN = register("no_cobweb_slowdown", DummyPower::new); //NO_COBWEB_SLOWDOWN & MASTER_OF_WEBS_NO_SLOWDOWN
	public static final RegistrySupplier<DummyPower> LIKE_WATER = register("like_water", DummyPower::new);
	public static final RegistrySupplier<DummyPower> WATER_VISION = register("water_vision", DummyPower::new); //TODO Might be worth transforming into a float field.
	public static final RegistrySupplier<DummyPower> CONDUIT_POWER_ON_LAND = register("conduit_power_on_land", DummyPower::new);
	public static final RegistrySupplier<DummyPower> SCARE_CREEPERS = register("scare_creepers", DummyPower::new);

	public static void initialize() { }

	@SuppressWarnings("unchecked")
	private static <T extends PowerFactory<?>> RegistrySupplier<T> register(String name, Supplier<T> factory) {
		return (RegistrySupplier<T>) OriginsRegistries.POWER_FACTORY.registerSupplied(Origins.identifier(name), factory::get);
	}

	public static void register() { }
}
