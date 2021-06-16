package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.DamageCondition;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;

import java.util.function.Function;

public final class ConfiguredEntityAction<T extends IOriginsFeatureConfiguration> extends ConfiguredFactory<T, EntityAction<T>> {
	public static final Codec<ConfiguredEntityAction<?>> CODEC = EntityAction.CODEC.dispatch(ConfiguredFactory::getFactory, Function.identity());

	public ConfiguredEntityAction(EntityAction<T> factory, T configuration) {
		super(factory, configuration);
	}

	public void execute(Entity entity) {
		this.getFactory().execute(this.getConfiguration(), entity);
	}
}
