package io.github.apace100.origins.api.power.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.BiomeCondition;
import io.github.apace100.origins.api.power.factory.BlockAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.function.Function;

public final class ConfiguredBlockAction<T extends IOriginsFeatureConfiguration> extends ConfiguredFactory<T, BlockAction<T>> {
	public static final Codec<ConfiguredBlockAction<?>> CODEC = BlockAction.CODEC.dispatch(ConfiguredFactory::getFactory, Function.identity());

	public ConfiguredBlockAction(BlockAction<T> factory, T configuration) {
		super(factory, configuration);
	}

	public void execute(World world, BlockPos pos, Direction direction) {
		this.getFactory().execute(this.getConfiguration(), world, pos, direction);
	}
}
