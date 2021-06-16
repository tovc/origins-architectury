package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.ConfiguredCondition;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.BlockAction;
import io.github.apace100.origins.api.power.factory.BlockCondition;
import net.minecraft.block.pattern.CachedBlockPosition;

import java.util.function.Function;

public final class ConfiguredBlockCondition<T extends IOriginsFeatureConfiguration> extends ConfiguredCondition<T, BlockCondition<T>> {
	public static final Codec<ConfiguredBlockCondition<?>> CODEC = BlockCondition.CODEC.dispatch(ConfiguredFactory::getFactory, Function.identity());

	public ConfiguredBlockCondition(BlockCondition<T> factory, T configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(CachedBlockPosition block) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), block);
	}
}