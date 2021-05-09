package io.github.apace100.origins.power.condition.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.biome.Biome;

import java.util.function.Predicate;

public class AmountCondition implements DamageCondition {

	public static final Codec<AmountCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(x -> x.comparison),
			Codec.FLOAT.fieldOf("compare_to").forGetter(x -> x.compareTo)
	).apply(instance, AmountCondition::new));

	private final Comparison comparison;
	private final float compareTo;

	public AmountCondition(Comparison comparison, float compareTo) {
		this.comparison = comparison;
		this.compareTo = compareTo;
	}
	@Override
	public boolean test(DamageSource source, float f) {
		return this.comparison.compare(f, this.compareTo);
	}
}
