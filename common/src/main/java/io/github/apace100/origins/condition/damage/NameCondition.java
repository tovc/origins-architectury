package io.github.apace100.origins.condition.damage;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.DamageCondition;
import net.minecraft.entity.damage.DamageSource;

public class NameCondition extends DamageCondition<FieldConfiguration<String>> {

	public NameCondition() {
		super(FieldConfiguration.codec(Codec.STRING, "name"));
	}

	@Override
	public boolean check(FieldConfiguration<String> configuration, DamageSource source, float amount) {
		return configuration.value().equals(source.name);
	}
}
