package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.FluidCondition;
import io.github.apace100.origins.api.power.factory.ItemAction;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

public final class ConfiguredItemAction<T extends IOriginsFeatureConfiguration> extends ConfiguredFactory<T, ItemAction<T>> {
	public static final Codec<ConfiguredItemAction<?>> CODEC = ItemAction.CODEC.dispatch(ConfiguredFactory::getFactory, Function.identity());

	public ConfiguredItemAction(ItemAction<T> factory, T configuration) {
		super(factory, configuration);
	}

	public void execute(ItemStack stack) {
		this.getFactory().execute(this.getConfiguration(), stack);
	}
}
