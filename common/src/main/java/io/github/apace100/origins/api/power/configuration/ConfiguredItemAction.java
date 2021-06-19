package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.ItemAction;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ConfiguredItemAction<C extends IOriginsFeatureConfiguration, F extends ItemAction<C>> extends ConfiguredFactory<C, F> {
	public static void execute(@Nullable ConfiguredItemAction<?, ?> action, ItemStack stack) {
		if (action != null) action.execute(stack);
	}

	public static final Codec<ConfiguredItemAction<?, ?>> CODEC = ItemAction.CODEC.dispatch(ConfiguredFactory::getFactory, Function.identity());

	public ConfiguredItemAction(F factory, C configuration) {
		super(factory, configuration);
	}

	public void execute(ItemStack stack) {
		this.getFactory().execute(this.getConfiguration(), stack);
	}
}
