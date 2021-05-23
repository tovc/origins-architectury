package io.github.apace100.origins.power.action.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

public class OffsetAction implements BlockAction {

	public static final Codec<OffsetAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.BLOCK_ACTION.fieldOf("action").forGetter(x -> x.action),
			Codec.INT.optionalFieldOf("x", 0).forGetter(x -> x.x),
			Codec.INT.optionalFieldOf("y", 0).forGetter(x -> x.y),
			Codec.INT.optionalFieldOf("z", 0).forGetter(x -> x.z)
	).apply(instance, OffsetAction::new));

	private final ActionFactory.Instance<Triple<World, BlockPos, Direction>> action;
	private final int x;
	private final int y;
	private final int z;

	public OffsetAction(ActionFactory.Instance<Triple<World, BlockPos, Direction>> action, int x, int y, int z) {
		this.action = action;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void accept(World world, BlockPos pos, Direction direction) {
		action.accept(Triple.of(world, pos.add(x, y, z), direction));
	}
}
