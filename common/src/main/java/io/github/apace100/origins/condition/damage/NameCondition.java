package io.github.apace100.origins.condition.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.damage.DamageSource;

import java.util.Objects;

public class NameCondition implements DamageCondition {

	public static final Codec<NameCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(x -> x.name)
	).apply(instance, NameCondition::new));

	private final String name;

	public NameCondition(String name) {
		this.name = name;
	}

	@Override
	public boolean test(DamageSource source, float f) {
		return Objects.equals(source.name, this.name);
	}
}
