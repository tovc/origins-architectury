package io.github.apace100.origins.power.action.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;

public class AddBlockAction implements BlockAction {

	public static final Codec<AddBlockAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_BLOCK.fieldOf("block").forGetter(x -> x.block)
	).apply(instance, AddBlockAction::new));

	private final Optional<Block> block;

	public AddBlockAction(Optional<Block> block) {this.block = block;}

	@Override
	public void accept(World world, BlockPos pos, Direction direction) {
		block.ifPresent(b -> world.setBlockState(pos.offset(direction), b.getDefaultState()));
	}
}
