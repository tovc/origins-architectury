package io.github.apace100.origins.condition.block;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.configuration.NoConfiguration;
import io.github.apace100.origins.api.power.factory.BlockCondition;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

import java.util.Arrays;

public class AttachableCondition extends BlockCondition<NoConfiguration> {

	public static final Codec<AttachableCondition> CODEC = Codec.unit(new AttachableCondition());

	public AttachableCondition() {
		super(NoConfiguration.CODEC);
	}

	@Override
	protected boolean check(NoConfiguration configuration, CachedBlockPosition block) {
		WorldView world = block.getWorld();
		BlockPos pos = block.getBlockPos();
		return Arrays.stream(Direction.values()).anyMatch(d -> world.getBlockState(pos.offset(d)).isSideSolidFullSquare(world, pos, d.getOpposite()));
	}
}
