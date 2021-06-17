package io.github.apace100.origins.power.factory;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.*;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.apace100.origins.util.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

public class PowerFactories {

    @SuppressWarnings("unchecked")
    public static void register() {
        register(new PowerFactory<>(Origins.identifier("toggle"),
            new SerializableData()
                .add("active_by_default", SerializableDataType.BOOLEAN, true)
                .add("key", SerializableDataType.BACKWARDS_COMPATIBLE_KEY, new Active.Key()),
            data ->
                (type, player) -> {
                    TogglePower power = new TogglePower(type, player, data.getBoolean("active_by_default"));
                    power.setKey(data.get("key"));
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("entity_group"),
            new SerializableData()
                .add("group", SerializableDataType.ENTITY_GROUP),
            data ->
                (type, player) -> new SetEntityGroupPower(type, player, data.get("group")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("exhaust"),
            new SerializableData()
                .add("interval", SerializableDataType.INT)
                .add("exhaustion", SerializableDataType.FLOAT),
            data ->
                (type, player) -> new ExhaustOverTimePower(type, player, data.getInt("interval"), data.getFloat("exhaustion")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("fire_projectile"),
            new SerializableData()
                .add("cooldown", SerializableDataType.INT)
                .add("count", SerializableDataType.INT, 1)
                .add("speed", SerializableDataType.FLOAT, 1.5F)
                .add("divergence", SerializableDataType.FLOAT, 1F)
                .add("sound", SerializableDataType.SOUND_EVENT, null)
                .add("entity_type", SerializableDataType.ENTITY_TYPE)
                .add("hud_render", SerializableDataType.HUD_RENDER)
                .add("tag", SerializableDataType.NBT, null)
                .add("key", SerializableDataType.BACKWARDS_COMPATIBLE_KEY, new Active.Key()),
            data ->
                (type, player) -> {
                    FireProjectilePower power = new FireProjectilePower(type, player,
                        data.getInt("cooldown"),
                        data.get("hud_render"),
                        data.get("entity_type"),
                        data.getInt("count"),
                        data.getFloat("speed"),
                        data.getFloat("divergence"),
                        data.get("sound"),
                        data.get("tag"));
                    power.setKey(data.get("key"));
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("inventory"),
            new SerializableData()
                .add("name", SerializableDataType.STRING, "container.inventory")
                .add("drop_on_death", SerializableDataType.BOOLEAN, false)
                .add("drop_on_death_filter", SerializableDataType.ITEM_CONDITION, null)
                .add("key", SerializableDataType.BACKWARDS_COMPATIBLE_KEY, new Active.Key()),
            data ->
                (type, player) -> {
                    InventoryPower power = new InventoryPower(type, player, data.get("name"), 9,
                        data.getBoolean("drop_on_death"),
                        data.isPresent("drop_on_death_filter") ? data.get("drop_on_death_filter") :
                            itemStack -> true);
                    power.setKey(data.get("key"));
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("invisibility"),
            new SerializableData()
                .add("render_armor", SerializableDataType.BOOLEAN),
            data ->
                (type, player) -> new InvisibilityPower(type, player, data.getBoolean("render_armor")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("invulnerability"),
            new SerializableData()
                .add("damage_condition", SerializableDataType.DAMAGE_CONDITION),
            data ->
                (type, player) -> {
                    ConditionFactory.Instance<Pair<DamageSource, Float>> damageCondition =
                            data.get("damage_condition");
                    return new InvulnerablePower(type, player, ds -> damageCondition.test(new Pair<>(ds, null)));
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("launch"),
            new SerializableData()
                .add("cooldown", SerializableDataType.INT)
                .add("speed", SerializableDataType.FLOAT)
                .add("sound", SerializableDataType.SOUND_EVENT, null)
                .add("hud_render", SerializableDataType.HUD_RENDER)
                .add("key", SerializableDataType.BACKWARDS_COMPATIBLE_KEY, new Active.Key()),
            data -> {
                SoundEvent soundEvent = data.get("sound");
                return (type, player) -> {
                    ActiveCooldownPower power = new ActiveCooldownPower(type, player,
                        data.getInt("cooldown"),
                            data.get("hud_render"),
                        e -> {
                            if (!e.world.isClient && e instanceof PlayerEntity) {
                                PlayerEntity p = (PlayerEntity) e;
                                p.addVelocity(0, data.getFloat("speed"), 0);
                                p.velocityModified = true;
                                if (soundEvent != null) {
                                    p.world.playSound(null, p.getX(), p.getY(), p.getZ(), soundEvent, SoundCategory.NEUTRAL, 0.5F, 0.4F / (p.getRandom().nextFloat() * 0.4F + 0.8F));
                                }
                                for (int i = 0; i < 4; ++i) {
                                    ((ServerWorld) p.world).spawnParticles(ParticleTypes.CLOUD, p.getX(), p.getRandomBodyY(), p.getZ(), 8, p.getRandom().nextGaussian(), 0.0D, p.getRandom().nextGaussian(), 0.5);
                                }
                            }
                        });
                    power.setKey(data.get("key"));
                    return power;
                };
            }).allowCondition());
        register(new PowerFactory<>(Origins.identifier("model_color"),
            new SerializableData()
                .add("red", SerializableDataType.FLOAT, 1.0F)
                .add("green", SerializableDataType.FLOAT, 1.0F)
                .add("blue", SerializableDataType.FLOAT, 1.0F)
                .add("alpha", SerializableDataType.FLOAT, 1.0F),
            data ->
                (type, player) ->
                    new ModelColorPower(type, player, data.getFloat("red"), data.getFloat("green"), data.getFloat("blue"), data.getFloat("alpha")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("modify_break_speed"),
            new SerializableData()
                .add("block_condition", SerializableDataType.BLOCK_CONDITION, null)
                .add("modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                .add("modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null),
            data ->
                (type, player) -> {
                    ModifyBreakSpeedPower power = new ModifyBreakSpeedPower(type, player, data.isPresent("block_condition") ? data.get("block_condition") : cbp -> true);
                    if(data.isPresent("modifier")) {
                        power.addModifier(data.get("modifier"));
                    }
                    if(data.isPresent("modifiers")) {
                        ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                    }
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("modify_damage_dealt"),
            new SerializableData()
                .add("damage_condition", SerializableDataType.DAMAGE_CONDITION, null)
                .add("modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                .add("modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null)
                .add("target_condition", SerializableDataType.ENTITY_CONDITION, null)
                .add("self_action", SerializableDataType.ENTITY_ACTION, null)
                .add("target_action", SerializableDataType.ENTITY_ACTION, null),
            data ->
                (type, player) -> {
                    ModifyDamageDealtPower power = new ModifyDamageDealtPower(type, player,
                        data.isPresent("damage_condition") ? data.get("damage_condition") : dmg -> true,
                        data.get("target_condition"));
                    if(data.isPresent("modifier")) {
                        power.addModifier(data.get("modifier"));
                    }
                    if(data.isPresent("modifiers")) {
                        ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                    }
                    if(data.isPresent("self_action")) {
                        power.setSelfAction(data.get("self_action"));
                    }
                    if(data.isPresent("target_action")) {
                        power.setTargetAction(data.get("target_action"));
                    }
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("modify_damage_taken"),
            new SerializableData()
                .add("damage_condition", SerializableDataType.DAMAGE_CONDITION, null)
                .add("modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                .add("modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null)
                .add("self_action", SerializableDataType.ENTITY_ACTION, null)
                .add("attacker_action", SerializableDataType.ENTITY_ACTION, null),
            data ->
                (type, player) -> {
                    ModifyDamageTakenPower power = new ModifyDamageTakenPower(type, player,
                        data.isPresent("damage_condition") ? data.get("damage_condition") : dmg -> true);
                    if(data.isPresent("modifier")) {
                        power.addModifier(data.get("modifier"));
                    }
                    if(data.isPresent("modifiers")) {
                        ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                    }
                    if(data.isPresent("self_action")) {
                        power.setSelfAction(data.get("self_action"));
                    }
                    if(data.isPresent("attacker_action")) {
                        power.setAttackerAction(data.get("attacker_action"));
                    }
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("modify_exhaustion"),
            new SerializableData()
                .add("modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                .add("modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null),
            data ->
                (type, player) -> {
                    ModifyExhaustionPower power = new ModifyExhaustionPower(type, player);
                    if(data.isPresent("modifier")) {
                        power.addModifier(data.get("modifier"));
                    }
                    if(data.isPresent("modifiers")) {
                        ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                    }
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("modify_harvest"),
            new SerializableData()
                .add("block_condition", SerializableDataType.BLOCK_CONDITION, null)
                .add("allow", SerializableDataType.BOOLEAN),
            data ->
                (type, player) ->
                    new ModifyHarvestPower(type, player,
                        data.isPresent("block_condition") ? data.get("block_condition") : cbp -> true,
                        data.getBoolean("allow")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("modify_jump"),
            new SerializableData()
                .add("modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                .add("modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null)
                .add("entity_action", SerializableDataType.ENTITY_ACTION, null),
            data ->
                (type, player) -> {
                    ModifyJumpPower power = new ModifyJumpPower(type, player, data.get("entity_action"));
                    if(data.isPresent("modifier")) {
                        power.addModifier(data.get("modifier"));
                    }
                    if(data.isPresent("modifiers")) {
                        ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                    }
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("modify_player_spawn"),
                new SerializableData()
                        .add("dimension", SerializableDataType.DIMENSION)
                        .add("dimension_distance_multiplier", SerializableDataType.FLOAT, 0F)
                        .add("biome", SerializableDataType.IDENTIFIER, null)
                        .add("spawn_strategy", SerializableDataType.STRING, "default")
                        .add("structure", SerializableDataType.registry(ClassUtil.castClass(StructureFeature.class), Registry.STRUCTURE_FEATURE), null)
                        .add("respawn_sound", SerializableDataType.SOUND_EVENT, null),
                data ->
                        (type, player) ->
                                new ModifyPlayerSpawnPower(type, player,
                                        data.get("dimension"),
                                        data.getFloat("dimension_distance_multiplier"),
                                        data.get("biome"),
                                        data.get("spawn_strategy"),
                                        data.isPresent("structure") ? data.get("structure") : null,
                                        data.get("respawn_sound")))
                .allowCondition());
        register(new PowerFactory<>(Origins.identifier("night_vision"),
            new SerializableData()
                .add("strength", SerializableDataType.FLOAT, 1.0F),
            data ->
                (type, player) ->
                    new NightVisionPower(type, player, data.getFloat("strength")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("particle"),
            new SerializableData()
                .add("particle", SerializableDataType.PARTICLE_TYPE)
                .add("frequency", SerializableDataType.INT),
            data ->
                (type, player) ->
                    new ParticlePower(type, player, data.get("particle"), data.getInt("frequency")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("phasing"),
            new SerializableData()
                .add("block_condition", SerializableDataType.BLOCK_CONDITION, null)
                .add("blacklist", SerializableDataType.BOOLEAN, false)
                .add("render_type", SerializableDataType.enumValue(PhasingPower.RenderType.class), PhasingPower.RenderType.BLINDNESS)
                .add("view_distance", SerializableDataType.FLOAT, 10F)
                .add("phase_down_condition", SerializableDataType.ENTITY_CONDITION, null),
            data ->
                (type, player) ->
                    new PhasingPower(type, player, data.isPresent("block_condition") ? data.get("block_condition") : cbp -> true,
                        data.getBoolean("blacklist"), data.get("render_type"), data.getFloat("view_distance"),
                            data.get("phase_down_condition")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("prevent_item_use"),
            new SerializableData()
                .add("item_condition", SerializableDataType.ITEM_CONDITION, null),
            data ->
                (type, player) ->
                    new PreventItemUsePower(type, player, data.isPresent("item_condition") ? data.get("item_condition") : item -> true))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("prevent_sleep"),
            new SerializableData()
                .add("block_condition", SerializableDataType.BLOCK_CONDITION, null)
                .add("message", SerializableDataType.STRING, "conditionedOrigins.cant_sleep")
                .add("set_spawn_point", SerializableDataType.BOOLEAN, false),
            data ->
                (type, player) ->
                    new PreventSleepPower(type, player,
                        data.isPresent("block_condition") ? data.get("block_condition") : cbp -> true,
                        data.get("message"), data.getBoolean("set_spawn_point")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("stacking_status_effect"),
            new SerializableData()
                .add("min_stacks", SerializableDataType.INT)
                .add("max_stacks", SerializableDataType.INT)
                .add("duration_per_stack", SerializableDataType.INT)
                .add("effect", SerializableDataType.STATUS_EFFECT_INSTANCE, null)
                .add("effects", SerializableDataType.STATUS_EFFECT_INSTANCES, null),
            data ->
                (type, player) -> {
                    StackingStatusEffectPower power = new StackingStatusEffectPower(type, player,
                        data.getInt("min_stacks"),
                        data.getInt("max_stacks"),
                        data.getInt("duration_per_stack"));
                    if(data.isPresent("effect")) {
                        power.addEffect((StatusEffectInstance)data.get("effect"));
                    }
                    if(data.isPresent("effects")) {
                        ((List<StatusEffectInstance>)data.get("effects")).forEach(power::addEffect);
                    }
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("modify_swim_speed"),
            new SerializableData()
                .add("modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                .add("modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null),
            data ->
                (type, player) -> {
                    ModifySwimSpeedPower power = new ModifySwimSpeedPower(type, player);
                    if(data.isPresent("modifier")) {
                        power.addModifier(data.get("modifier"));
                    }
                    if(data.isPresent("modifiers")) {
                        ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                    }
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("toggle_night_vision"),
            new SerializableData()
                .add("active_by_default", SerializableDataType.BOOLEAN, false)
                .add("strength", SerializableDataType.FLOAT, 1.0F)
                .add("key", SerializableDataType.BACKWARDS_COMPATIBLE_KEY, new Active.Key()),
            data ->
                (type, player) -> {
                    ToggleNightVisionPower power = new ToggleNightVisionPower(type, player, data.getFloat("strength"), data.getBoolean("active_by_default"));
                    power.setKey(data.get("key"));
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("modify_lava_speed"),
            new SerializableData()
                .add("modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                .add("modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null),
            data ->
                (type, player) -> {
                    ModifyLavaSpeedPower power = new ModifyLavaSpeedPower(type, player);
                    if(data.isPresent("modifier")) {
                        power.addModifier(data.get("modifier"));
                    }
                    if(data.isPresent("modifiers")) {
                        ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                    }
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("lava_vision"),
            new SerializableData()
                .add("s", SerializableDataType.FLOAT)
                .add("v", SerializableDataType.FLOAT),
            data ->
                (type, player) ->
                    new LavaVisionPower(type, player, data.getFloat("s"), data.getFloat("v")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("self_action_on_hit"),
            new SerializableData()
                .add("entity_action", SerializableDataType.ENTITY_ACTION)
                .add("damage_condition", SerializableDataType.DAMAGE_CONDITION, null)
                .add("cooldown", SerializableDataType.INT)
                .add("hud_render", SerializableDataType.HUD_RENDER, HudRender.DONT_RENDER)
                .add("target_condition", SerializableDataType.ENTITY_CONDITION, null),
            data ->
                (type, player) -> new SelfActionOnHitPower(type, player, data.getInt("cooldown"),
                    data.get("hud_render"), data.get("damage_condition"),
                    data.get("entity_action"),
                    data.get("target_condition")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("target_action_on_hit"),
            new SerializableData()
                .add("entity_action", SerializableDataType.ENTITY_ACTION)
                .add("damage_condition", SerializableDataType.DAMAGE_CONDITION, null)
                .add("cooldown", SerializableDataType.INT)
                .add("hud_render", SerializableDataType.HUD_RENDER, HudRender.DONT_RENDER)
                .add("target_condition", SerializableDataType.ENTITY_CONDITION, null),
            data ->
                (type, player) -> new TargetActionOnHitPower(type, player, data.getInt("cooldown"),
                    data.get("hud_render"), data.get("damage_condition"),
                    data.get("entity_action"),
                    data.get("target_condition")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("starting_equipment"),
            new SerializableData()
                .add("stack", SerializableDataType.POSITIONED_ITEM_STACK, null)
                .add("stacks", SerializableDataType.POSITIONED_ITEM_STACKS, null)
                .add("recurrent", SerializableDataType.BOOLEAN, false),
            data ->
                (type, player) -> {
                    StartingEquipmentPower power = new StartingEquipmentPower(type, player);
                    if(data.isPresent("stack")) {
                        Pair<Integer, ItemStack> stack = data.get("stack");
                        int slot = stack.getLeft();
                        if(slot > Integer.MIN_VALUE) {
                            power.addStack(stack.getLeft(), stack.getRight());
                        } else {
                            power.addStack(stack.getRight());
                        }
                    }
                    if(data.isPresent("stacks")) {
                        ((List<Pair<Integer, ItemStack>>)data.get("stacks"))
                            .forEach(integerItemStackPair -> {
                                int slot = integerItemStackPair.getLeft();
                                if(slot > Integer.MIN_VALUE) {
                                    power.addStack(integerItemStackPair.getLeft(), integerItemStackPair.getRight());
                                } else {
                                    power.addStack(integerItemStackPair.getRight());
                                }
                            });
                    }
                    power.setRecurrent(data.getBoolean("recurrent"));
                    return power;
                }));
        register(new PowerFactory<>(Origins.identifier("walk_on_fluid"),
            new SerializableData()
                .add("fluid", SerializableDataType.FLUID_TAG),
            data ->
                (type, player) -> new WalkOnFluidPower(type, player, data.get("fluid")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("shader"),
            new SerializableData()
                .add("shader", SerializableDataType.IDENTIFIER),
            data ->
                (type, player) -> new ShaderPower(type, player, data.get("shader")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("resource"),
            new SerializableData()
                .add("min", SerializableDataType.INT)
                .add("max", SerializableDataType.INT)
                .addFunctionedDefault("start_value", SerializableDataType.INT, data -> data.getInt("min"))
                .add("hud_render", SerializableDataType.HUD_RENDER)
                .add("min_action", SerializableDataType.ENTITY_ACTION, null)
                .add("max_action", SerializableDataType.ENTITY_ACTION, null),
            data ->
                (type, player) ->
                    new ResourcePower(type, player,
                        data.get("hud_render"),
                        data.getInt("start_value"),
                        data.getInt("min"),
                        data.getInt("max"),
                        data.get("min_action"),
                        data.get("max_action")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("modify_food"),
            new SerializableData()
                .add("item_condition", SerializableDataType.ITEM_CONDITION, null)
                .add("food_modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                .add("food_modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null)
                .add("saturation_modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                .add("saturation_modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null)
                .add("entity_action", SerializableDataType.ENTITY_ACTION, null),
            data ->
                (type, player) -> {
                    List<EntityAttributeModifier> foodModifiers = new LinkedList<>();
                    List<EntityAttributeModifier> saturationModifiers = new LinkedList<>();
                    if(data.isPresent("food_modifier")) {
                        foodModifiers.add(data.get("food_modifier"));
                    }
                    if(data.isPresent("food_modifiers")) {
                        List<EntityAttributeModifier> modifierList = data.get("food_modifiers");
                        foodModifiers.addAll(modifierList);
                    }
                    if(data.isPresent("saturation_modifier")) {
                        saturationModifiers.add(data.get("saturation_modifier"));
                    }
                    if(data.isPresent("saturation_modifiers")) {
                        List<EntityAttributeModifier> modifierList = data.get("saturation_modifiers");
                        saturationModifiers.addAll(modifierList);
                    }
                    return new ModifyFoodPower(type, player, data.isPresent("item_condition") ? data.get("item_condition") : stack -> true,
                        foodModifiers, saturationModifiers, data.get("entity_action"));
                }).allowCondition());
        register(new PowerFactory<>(Origins.identifier("modify_xp_gain"),
            new SerializableData()
                .add("modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                .add("modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null),
            data ->
                (type, player) -> {
                    ModifyExperiencePower power = new ModifyExperiencePower(type, player);
                    if(data.isPresent("modifier")) {
                        power.addModifier(data.get("modifier"));
                    }
                    if(data.isPresent("modifiers")) {
                        (data.<List<EntityAttributeModifier>>get("modifiers")).forEach(power::addModifier);
                    }
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("prevent_entity_render"),
            new SerializableData()
                .add("entity_condition", SerializableDataType.ENTITY_CONDITION, null),
            data ->
                (type, player) -> new PreventEntityRenderPower(type, player, data.get("entity_condition")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("prevent_block_selection"),
            new SerializableData()
                .add("block_condition", SerializableDataType.BLOCK_CONDITION, null),
            data ->
                (type, player) -> new PreventBlockSelectionPower(type, player,
                    data.get("block_condition")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("self_action_on_kill"),
            new SerializableData()
                .add("entity_action", SerializableDataType.ENTITY_ACTION)
                .add("damage_condition", SerializableDataType.DAMAGE_CONDITION, null)
                .add("cooldown", SerializableDataType.INT)
                .add("hud_render", SerializableDataType.HUD_RENDER, HudRender.DONT_RENDER)
                .add("target_condition", SerializableDataType.ENTITY_CONDITION, null),
            data ->
                (type, player) -> new SelfActionOnKillPower(type, player, data.getInt("cooldown"),
                    data.get("hud_render"), data.get("damage_condition"),
                    data.get("entity_action"),
                    data.get("target_condition")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("recipe"),
            new SerializableData()
                .add("recipe", SerializableDataType.RECIPE),
            data ->
                (type, player) -> {
                    Recipe<CraftingInventory> recipe = data.get("recipe");
                    return new RecipePower(type, player, recipe);
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("modify_projectile_damage"),
            new SerializableData()
                .add("damage_condition", SerializableDataType.DAMAGE_CONDITION, null)
                .add("modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                .add("modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null)
                .add("target_condition", SerializableDataType.ENTITY_CONDITION, null)
                .add("self_action", SerializableDataType.ENTITY_ACTION, null)
                .add("target_action", SerializableDataType.ENTITY_ACTION, null),
            data ->
                (type, player) -> {
                    ModifyProjectileDamagePower power = new ModifyProjectileDamagePower(type, player,
                        data.isPresent("damage_condition") ? data.get("damage_condition") : dmg -> true,
                            data.get("target_condition"));
                    if(data.isPresent("modifier")) {
                        power.addModifier(data.get("modifier"));
                    }
                    if(data.isPresent("modifiers")) {
                        ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                    }
                    if(data.isPresent("self_action")) {
                        power.setSelfAction(data.get("self_action"));
                    }
                    if(data.isPresent("target_action")) {
                        power.setTargetAction(data.get("target_action"));
                    }
                    return power;
                })
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("prevent_block_use"),
            new SerializableData()
                .add("block_condition", SerializableDataType.BLOCK_CONDITION, null),
            data ->
                (type, player) -> new PreventBlockUsePower(type, player,
                    data.get("block_condition")))
            .allowCondition());
        register(new PowerFactory<>(Origins.identifier("prevent_death"),
                new SerializableData()
                        .add("entity_action", SerializableDataType.ENTITY_ACTION, null)
                        .add("damage_condition", SerializableDataType.DAMAGE_CONDITION, null),
                data ->
                        (type, player) -> new PreventDeathPower(type, player,
                                data.get("entity_action"),
                                data.get("damage_condition"))) {}
            .allowCondition());
    }

    private static void register(PowerFactory<?> serializer) {
        ModRegistriesArchitectury.POWER_FACTORY.register(serializer.getSerializerId(), () -> serializer);
    }
}
