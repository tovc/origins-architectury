package io.github.apace100.origins.power.factory.condition;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.power.condition.biome.HighHumidityCondition;
import io.github.apace100.origins.power.condition.biome.StringBiomeCondition;
import io.github.apace100.origins.power.factory.MetaFactories;
import io.github.apace100.origins.power.factory.meta.condition.FloatComparingCondition;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.world.biome.Biome;

import java.util.function.Predicate;

public class BiomeConditions {

	public static void register() {
		MetaFactories.defineMetaConditions(ModRegistriesArchitectury.BIOME_CONDITION, OriginsCodecs.BIOME_CONDITION);
		register("high_humidity", HighHumidityCondition.CODEC);
		register("temperature", FloatComparingCondition.codec(Biome::getTemperature));
		register("category", StringBiomeCondition.codec("category", biome -> biome.getCategory().getName()));
		register("precipitation", StringBiomeCondition.codec("precipitation", biome -> biome.getPrecipitation().getName()));
	}

	private static void register(String name, Codec<? extends Predicate<Biome>> codec) {
		ModRegistriesArchitectury.BIOME_CONDITION.registerSupplied(Origins.identifier(name), () -> new ConditionFactory<>(codec));
	}
}
