package io.github.apace100.origins.action.block;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.action.meta.IDelegatedActionConfiguration;
import io.github.apace100.origins.api.power.factory.BlockAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

public class DelegatedBlockAction<T extends IDelegatedActionConfiguration<Triple<World, BlockPos, Direction>>> extends BlockAction<T> {

	public DelegatedBlockAction(Codec<T> codec) {
		super(codec);
	}

	@Override
	public void execute(T configuration, World world, BlockPos pos, Direction direction) {
		configuration.execute(Triple.of(world, pos, direction));
	}
}
