package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.Consumer;

public class BlockActionAtAction implements Consumer<Entity> {

	public static final Codec<BlockActionAtAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.BLOCK_ACTION.fieldOf("block_action").forGetter(x -> x.action)
	).apply(instance, BlockActionAtAction::new));

	public final ActionFactory.Instance<Triple<World, BlockPos, Direction>> action;

	public BlockActionAtAction(ActionFactory.Instance<Triple<World, BlockPos, Direction>> action) {this.action = action;}

	@Override
	public void accept(Entity entity) {
		action.accept(Triple.of(entity.world, entity.getBlockPos(), Direction.UP));
	}
}
