package io.github.apace100.origins.power.factory.condition;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.entity.AdvancementCondition;
import io.github.apace100.origins.power.condition.entity.GameModeCondition;
import io.github.apace100.origins.power.condition.entity.UsingEffectiveToolCondition;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import net.minecraft.entity.LivingEntity;

import java.util.function.Predicate;

public final class EntityConditionsServer {

	public static void register() {
		register("using_effective_tool", Codec.unit(UsingEffectiveToolCondition::new));
		register("gamemode", GameModeCondition.codec(GameModeCondition::new));
		register("advancement", AdvancementCondition.codec(AdvancementCondition::new));
	}

	private static void register(String name, Codec<? extends Predicate<LivingEntity>> codec) {
		ModRegistriesArchitectury.ENTITY_CONDITION.registerSupplied(Origins.identifier(name), () -> new ConditionFactory<>(codec));
	}
}
