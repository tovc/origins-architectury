package io.github.apace100.origins.condition.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;

import java.util.function.Predicate;

public class UsingEffectiveToolCondition implements Predicate<LivingEntity> {

	protected boolean testClient(LivingEntity entity) {
		return false;
	}

	@Override
	public boolean test(LivingEntity entity) {
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerInteractionManager interactionMngr = ((ServerPlayerEntity) entity).interactionManager;
			if (interactionMngr.mining) {
				return ((PlayerEntity) entity).isUsingEffectiveTool(entity.world.getBlockState(interactionMngr.miningPos));
			}
		}
		return testClient(entity);
	}

	@Environment(EnvType.CLIENT)
	public static class Client extends UsingEffectiveToolCondition {
		@Override
		protected boolean testClient(LivingEntity entity) {
			if (entity instanceof ClientPlayerEntity) {
				ClientPlayerInteractionManager interactionMngr = MinecraftClient.getInstance().interactionManager;
				if (interactionMngr.isBreakingBlock())
					return ((PlayerEntity) entity).isUsingEffectiveTool(entity.world.getBlockState(interactionMngr.currentBreakingPos));
			}
			return false;
		}
	}
}
