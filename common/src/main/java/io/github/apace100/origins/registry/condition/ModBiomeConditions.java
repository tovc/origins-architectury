package io.github.apace100.origins.registry.condition;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.configuration.ConfiguredBiomeCondition;
import io.github.apace100.origins.api.power.factory.BiomeCondition;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import io.github.apace100.origins.condition.biome.DelegatedBiomeCondition;
import io.github.apace100.origins.condition.biome.FloatComparingBiomeCondition;
import io.github.apace100.origins.condition.biome.HighHumidityCondition;
import io.github.apace100.origins.condition.biome.StringBiomeCondition;
import io.github.apace100.origins.factory.MetaFactories;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.world.biome.Biome;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class ModBiomeConditions {
	public static final BiPredicate<ConfiguredBiomeCondition<?, ?>, Biome> PREDICATE = (config, biome) -> config.check(biome);

	public static final RegistrySupplier<StringBiomeCondition> CATEGORY = register("category", () -> new StringBiomeCondition("category", biome -> biome.getCategory().getName()));
	public static final RegistrySupplier<HighHumidityCondition> HIGH_HUMIDITY = register("high_humidity", HighHumidityCondition::new);
	public static final RegistrySupplier<StringBiomeCondition> PRECIPITATION = register("precipitation", () -> new StringBiomeCondition("precipitation", biome -> biome.getCategory().getName()));
	public static final RegistrySupplier<FloatComparingBiomeCondition> TEMPERATURE = register("temperature", () -> new FloatComparingBiomeCondition(Biome::getTemperature));

	public static void register() {
		MetaFactories.defineMetaConditions(OriginsRegistries.BIOME_CONDITION, DelegatedBiomeCondition::new, ConfiguredBiomeCondition.CODEC, PREDICATE);
	}

	@SuppressWarnings("unchecked")
	private static <T extends BiomeCondition<?>> RegistrySupplier<T> register(String name, Supplier<T> factory) {
		return (RegistrySupplier<T>) OriginsRegistries.BIOME_CONDITION.registerSupplied(Origins.identifier(name), factory::get);
	}
}
