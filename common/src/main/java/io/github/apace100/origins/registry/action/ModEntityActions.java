package io.github.apace100.origins.registry.action;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.action.entity.*;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import io.github.apace100.origins.api.power.factory.EntityAction;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import io.github.apace100.origins.factory.MetaFactories;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class ModEntityActions {
	public static final BiConsumer<ConfiguredEntityAction<?, ?>, Entity> EXECUTOR = (action, entity) -> action.execute(entity);
	public static final BiPredicate<ConfiguredEntityCondition<?, ?>, Entity> PREDICATE = (condition, entity) -> entity instanceof LivingEntity le && condition.check(le);

	public static final RegistrySupplier<AddVelocityAction> ADD_VELOCITY = register("add_velocity", AddVelocityAction::new);
	public static final RegistrySupplier<AddExperienceAction> ADD_EXPERIENCE = register("add_xp", AddExperienceAction::new);
	public static final RegistrySupplier<ApplyEffectAction> APPLY_EFFECT = register("apply_effect", ApplyEffectAction::new);
	public static final RegistrySupplier<BlockActionAtAction> BLOCK_ACTION_AT = register("block_action_at", BlockActionAtAction::new);
	public static final RegistrySupplier<ChangeResourceAction> CHANGE_RESOURCE = register("change_resource", ChangeResourceAction::new);
	public static final RegistrySupplier<ClearEffectAction> CLEAR_EFFECT = register("clear_effect", ClearEffectAction::new);
	public static final RegistrySupplier<SimpleEntityAction> EXTINGUISH = register("extinguish", () -> new SimpleEntityAction(Entity::extinguish));
	public static final RegistrySupplier<ExecuteCommandEntityAction> EXECUTE_COMMAND = register("execute_command", ExecuteCommandEntityAction::new);
	public static final RegistrySupplier<IntegerEntityAction> SET_ON_FIRE = register("set_on_fire", () -> new IntegerEntityAction(Entity::setOnFireFor, "duration"));
	public static final RegistrySupplier<FloatEntityAction> EXHAUST = register("exhaust", () -> FloatEntityAction.ofPlayer((x, f) -> x.getHungerManager().addExhaustion(f), "amount"));
	public static final RegistrySupplier<FloatEntityAction> HEAL = register("heal", () -> FloatEntityAction.ofLiving(LivingEntity::heal, "amount"));
	public static final RegistrySupplier<IntegerEntityAction> GAIN_AIR = register("gain_air", () -> IntegerEntityAction.ofLiving((x, f) -> x.setAir(x.getAir() + f), "value"));
	public static final RegistrySupplier<FloatEntityAction> SET_FALL_DISTANCE = register("set_fall_distance", () -> new FloatEntityAction((entity, f) -> entity.fallDistance = f, "fall_distance"));
	public static final RegistrySupplier<DamageAction> DAMAGE = register("damage", DamageAction::new);
	public static final RegistrySupplier<EquippedItemAction> EQUIPPED_ITEM_ACTION = register("equipped_item_action", EquippedItemAction::new);
	public static final RegistrySupplier<FeedAction> FEED = register("feed", FeedAction::new);
	public static final RegistrySupplier<GiveAction> GIVE = register("give", GiveAction::new);
	public static final RegistrySupplier<PlaySoundAction> PLAY_SOUND = register("play_sound", PlaySoundAction::new);
	public static final RegistrySupplier<SpawnEffectCloudAction> SPAWN_EFFECT_CLOUD = register("spawn_effect_cloud", SpawnEffectCloudAction::new);
	public static final RegistrySupplier<SpawnEntityAction> SPAWN_ENTITY = register("spawn_entity", SpawnEntityAction::new);
	public static final RegistrySupplier<TriggerCooldownAction> TRIGGER_COOLDOWN = register("trigger_cooldown", TriggerCooldownAction::new);

	public static void register() {
		MetaFactories.defineMetaActions(OriginsRegistries.ENTITY_ACTION, DelegatedEntityAction::new, ConfiguredEntityAction.CODEC, ConfiguredEntityCondition.CODEC, EXECUTOR, PREDICATE);
	}

	@SuppressWarnings("unchecked")
	private static <T extends EntityAction<?>> RegistrySupplier<T> register(String name, Supplier<T> factory) {
		return (RegistrySupplier<T>) OriginsRegistries.ENTITY_ACTION.registerSupplied(Origins.identifier(name), factory::get);
	}
}
