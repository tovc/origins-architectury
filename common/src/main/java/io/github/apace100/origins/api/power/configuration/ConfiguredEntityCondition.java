package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.ConfiguredCondition;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ConfiguredEntityCondition<C extends IOriginsFeatureConfiguration, F extends EntityCondition<C>> extends ConfiguredCondition<C, F> {
	public static boolean check(@Nullable ConfiguredEntityCondition<?, ?> condition, LivingEntity entity) {
		return condition == null || condition.check(entity);
	}

	public static final Codec<ConfiguredEntityCondition<?, ?>> CODEC = EntityCondition.CODEC.dispatch(ConfiguredFactory::getFactory, Function.identity());

	public ConfiguredEntityCondition(F factory, C configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(LivingEntity entity) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), entity);
	}
}