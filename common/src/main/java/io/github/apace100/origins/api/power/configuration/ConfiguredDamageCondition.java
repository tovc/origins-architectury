package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.ConfiguredCondition;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.DamageCondition;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ConfiguredDamageCondition<C extends IOriginsFeatureConfiguration, F extends DamageCondition<C>> extends ConfiguredCondition<C, F> {
	public static final Codec<ConfiguredDamageCondition<?, ?>> CODEC = DamageCondition.CODEC.dispatch(ConfiguredDamageCondition::getFactory, Function.identity());

	public static boolean check(@Nullable ConfiguredDamageCondition<?, ?> condition, DamageSource damageSource, float amount) {
		return condition == null || condition.check(damageSource, amount);
	}

	public ConfiguredDamageCondition(F factory, C configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(DamageSource source, float amount) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), source, amount);
	}
}