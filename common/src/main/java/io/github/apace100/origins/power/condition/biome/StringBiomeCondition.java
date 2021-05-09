package io.github.apace100.origins.power.condition.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.biome.Biome;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class StringBiomeCondition implements Predicate<Biome> {

	public static Codec<StringBiomeCondition> codec(String name, Function<Biome, String> access) {
		return RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf(name).forGetter(x -> x.value)
		).apply(instance, s -> new StringBiomeCondition(s, access)));
	}

	private final String value;
	private final Function<Biome, String> access;

	public StringBiomeCondition(String value, Function<Biome, String> access) {
		this.value = value;
		this.access = access;
	}

	@Override
	public boolean test(Biome biome) {
		return Objects.equals(access.apply(biome), this.value);
	}
}
