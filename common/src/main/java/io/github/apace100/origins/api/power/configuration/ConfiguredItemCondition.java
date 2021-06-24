package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.ConfiguredCondition;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.ItemCondition;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ConfiguredItemCondition<C extends IOriginsFeatureConfiguration, F extends ItemCondition<C>> extends ConfiguredCondition<C, F> {
	public static final Codec<ConfiguredItemCondition<?, ?>> CODEC = ItemCondition.CODEC.dispatch(ConfiguredItemCondition::getFactory, Function.identity());

	public static boolean check(@Nullable ConfiguredItemCondition<?, ?> condition, ItemStack stack) {
		return condition == null || condition.check(stack);
	}

	public ConfiguredItemCondition(F factory, C configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(ItemStack stack) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), stack);
	}
}