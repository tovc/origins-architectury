package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ConfiguredEntityAction<C extends IOriginsFeatureConfiguration, F extends EntityAction<C>> extends ConfiguredFactory<C, F> {
	public static final Codec<ConfiguredEntityAction<?, ?>> CODEC = EntityAction.CODEC.dispatch(ConfiguredFactory::getFactory, Function.identity());

	public static void execute(@Nullable ConfiguredEntityAction<?, ?> action, Entity entity) {
		if (action != null)
			action.execute(entity);
	}

	public ConfiguredEntityAction(F factory, C configuration) {
		super(factory, configuration);
	}

	public void execute(Entity entity) {
		this.getFactory().execute(this.getConfiguration(), entity);
	}
}
