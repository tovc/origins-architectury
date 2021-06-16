package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.ConfiguredCondition;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.ItemAction;
import io.github.apace100.origins.api.power.factory.ItemCondition;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

public final class ConfiguredItemCondition<T extends IOriginsFeatureConfiguration> extends ConfiguredCondition<T, ItemCondition<T>> {
	public static final Codec<ConfiguredItemCondition<?>> CODEC = ItemCondition.CODEC.dispatch(ConfiguredFactory::getFactory, Function.identity());

	public ConfiguredItemCondition(ItemCondition<T> factory, T configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(ItemStack stack) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), stack);
	}
}