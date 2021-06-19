package io.github.apace100.origins.condition.damage;

import com.mojang.serialization.Codec;
import net.minecraft.entity.damage.DamageSource;

import java.util.function.Predicate;

public class DamageTypeCondition {

	public static Codec<DamageCondition> codec(Predicate<DamageSource> predicate) {
		return Codec.unit((s, f) -> predicate.test(s));
	}
}
