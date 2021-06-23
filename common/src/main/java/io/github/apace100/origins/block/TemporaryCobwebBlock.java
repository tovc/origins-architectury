package io.github.apace100.origins.block;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public class TemporaryCobwebBlock extends CobwebBlock {

	public TemporaryCobwebBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		worldIn.getBlockTickScheduler().schedule(pos, this, 60);
		super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!world.isClient())
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (OriginComponent.hasPower(entityIn, ModPowers.WEBBING.get()))
			return;
		super.onEntityCollision(state, worldIn, pos, entityIn);
	}
}