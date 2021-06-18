package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.ConfiguredCondition;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.BlockAction;
import io.github.apace100.origins.api.power.factory.BlockCondition;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ConfiguredBlockCondition<T extends IOriginsFeatureConfiguration, F extends BlockCondition<T>> extends ConfiguredCondition<T, F> {
	public static boolean check(@Nullable ConfiguredBlockCondition<?, ?> condition, CachedBlockPosition position) {
		return condition == null || condition.check(position);
	}
	public static final Codec<ConfiguredBlockCondition<?, ?>> CODEC = BlockCondition.CODEC.dispatch(ConfiguredFactory::getFactory, Function.identity());

	public ConfiguredBlockCondition(F factory, T configuration, ConditionData data) {
		super(factory, configuration, data);
	}

	public boolean check(CachedBlockPosition block) {
		return this.getFactory().check(this.getConfiguration(), this.getData(), block);
	}
}