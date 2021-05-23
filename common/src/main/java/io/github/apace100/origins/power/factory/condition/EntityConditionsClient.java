package io.github.apace100.origins.power.factory.condition;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.entity.AdvancementCondition;
import io.github.apace100.origins.power.condition.entity.GameModeCondition;
import io.github.apace100.origins.power.condition.entity.UsingEffectiveToolCondition;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;

import java.util.function.Predicate;

public final class EntityConditionsClient {

	@Environment(EnvType.CLIENT)
	public static void register() {
		register("using_effective_tool", Codec.unit(UsingEffectiveToolCondition.Client::new));
		register("gamemode", GameModeCondition.codec(GameModeCondition.Client::new));
		register("advancement", AdvancementCondition.codec(AdvancementCondition.Client::new));
	}

	private static void register(String name, Codec<? extends Predicate<LivingEntity>> codec) {
		ModRegistriesArchitectury.ENTITY_CONDITION.registerSupplied(Origins.identifier(name), () -> new ConditionFactory<>(codec));
	}
}
