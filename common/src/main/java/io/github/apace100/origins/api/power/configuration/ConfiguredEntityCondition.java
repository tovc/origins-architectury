package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.ConfiguredCondition;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.EntityAction;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import net.minecraft.entity.LivingEntity;

import java.util.function.Function;

public final class ConfiguredEntityCondition<T extends IOriginsFeatureConfiguration> extends ConfiguredCondition<T, EntityCondition<T>> {
	public static final Codec<ConfiguredEntityCondition<?>> CODEC = EntityCondition.CODEC.dispatch(ConfiguredFactory::getFactory, Function.identity());

	public ConfiguredEntityCondition(EntityCondition<T> factory, T configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(LivingEntity entity) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), entity);
	}
}