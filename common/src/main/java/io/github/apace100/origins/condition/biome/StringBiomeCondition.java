package io.github.apace100.origins.condition.biome;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.BiomeCondition;
import net.minecraft.world.biome.Biome;

import java.util.Objects;
import java.util.function.Function;

public class StringBiomeCondition extends BiomeCondition<FieldConfiguration<String>> {

	private final Function<Biome, String> function;

	public StringBiomeCondition(String field, Function<Biome, String> function) {
		super(FieldConfiguration.codec(Codec.STRING, field));
		this.function = function;
	}

	@Override
	protected boolean check(FieldConfiguration<String> configuration, Biome biome) {
		return Objects.equals(function.apply(biome), configuration.value());
	}
}
