package io.github.apace100.origins.api.power.factory;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.IFactory;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockAction;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import me.shedaniel.architectury.core.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class BlockAction<T extends IOriginsFeatureConfiguration> extends RegistryEntry<BlockAction<?>> implements IFactory<T, ConfiguredBlockAction<T, ?>, BlockAction<T>> {
	public static final Codec<BlockAction<?>> CODEC = OriginsRegistries.codec(OriginsRegistries.BLOCK_ACTION);

	private final Codec<T> codec;

	protected BlockAction(Codec<T> codec) {
		this.codec = codec;
	}

	@Override
	public Codec<T> getCodec() {
		return codec;
	}

	@Override
	public final ConfiguredBlockAction<T, ?> configure(T input) {
		return new ConfiguredBlockAction<>(this, input);
	}

	public abstract void execute(T configuration, World world, BlockPos pos, Direction direction);
}
