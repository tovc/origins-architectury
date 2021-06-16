package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.ConfiguredCondition;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.BlockCondition;
import io.github.apace100.origins.api.power.factory.DamageCondition;
import net.minecraft.entity.damage.DamageSource;

import java.util.function.Function;

public final class ConfiguredDamageCondition<T extends IOriginsFeatureConfiguration> extends ConfiguredCondition<T, DamageCondition<T>> {
	public static final Codec<ConfiguredDamageCondition<?>> CODEC = DamageCondition.CODEC.dispatch(ConfiguredFactory::getFactory, Function.identity());

	public ConfiguredDamageCondition(DamageCondition<T> factory, T configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(DamageSource source, float amount) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), source, amount);
	}
}