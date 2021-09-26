package io.github.apace100.origins.power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.origins.Origins;
import io.github.edwinmindcraft.apoli.common.power.DummyPower;
import io.github.edwinmindcraft.origins.common.power.NoSlowdownPower;
import io.github.edwinmindcraft.origins.common.registry.OriginRegisters;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fmllegacy.RegistryObject;

@SuppressWarnings("unchecked")
public class OriginsPowerTypes {

    public static final PowerType<?> LIKE_WATER = new PowerTypeReference<>(Origins.identifier("like_water"));
    public static final RegistryObject<DummyPower> WATER_BREATHING = OriginRegisters.POWER_FACTORIES.register("water_breathing", DummyPower::new);
    public static final RegistryObject<DummyPower> SCARE_CREEPERS = OriginRegisters.POWER_FACTORIES.register("scare_creepers", DummyPower::new);
    public static final RegistryObject<DummyPower> WATER_VISION = OriginRegisters.POWER_FACTORIES.register("water_vision", DummyPower::new);
    public static final RegistryObject<NoSlowdownPower> NO_SLOWDOWN = OriginRegisters.POWER_FACTORIES.register("no_slowdown", NoSlowdownPower::new);
    public static final RegistryObject<DummyPower> CONDUIT_POWER_ON_LAND = OriginRegisters.POWER_FACTORIES.register("conduit_power_on_land", DummyPower::new);

    public static void register() {
        register(new PowerFactory<>(Origins.identifier("action_on_callback"),
            new SerializableData()
                .add("entity_action_respawned", ApoliDataTypes.ENTITY_ACTION, null)
                .add("entity_action_removed", ApoliDataTypes.ENTITY_ACTION, null)
                .add("entity_action_gained", ApoliDataTypes.ENTITY_ACTION, null)
                .add("entity_action_lost", ApoliDataTypes.ENTITY_ACTION, null)
                .add("entity_action_added", ApoliDataTypes.ENTITY_ACTION, null)
                .add("entity_action_chosen", ApoliDataTypes.ENTITY_ACTION, null)
                .add("execute_chosen_when_orb", SerializableDataTypes.BOOLEAN, true),
            data ->
                (type, player) -> new OriginsCallbackPower(type, player,
                    (ActionFactory<Entity>.Instance)data.get("entity_action_respawned"),
                    (ActionFactory<Entity>.Instance)data.get("entity_action_removed"),
                    (ActionFactory<Entity>.Instance)data.get("entity_action_gained"),
                    (ActionFactory<Entity>.Instance)data.get("entity_action_lost"),
                    (ActionFactory<Entity>.Instance)data.get("entity_action_added"),
                    (ActionFactory<Entity>.Instance)data.get("entity_action_chosen"),
                    data.getBoolean("execute_chosen_when_orb")))
            .allowCondition());
    }

    private static void register(PowerFactory<?> serializer) {
        Registry.register(ApoliRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
    }
}
