package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.configuration.NoConfiguration;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;

public class UsingEffectiveToolCondition extends EntityCondition<NoConfiguration> {

	public UsingEffectiveToolCondition() {
		super(NoConfiguration.CODEC);
	}

	protected boolean checkClient(LivingEntity entity) {
		return false;
	}

	@Override
	public boolean check(NoConfiguration configuration, LivingEntity entity) {
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerInteractionManager interactionMngr = ((ServerPlayerEntity) entity).interactionManager;
			if (interactionMngr.mining) {
				return ((PlayerEntity) entity).isUsingEffectiveTool(entity.world.getBlockState(interactionMngr.miningPos));
			}
		}
		return checkClient(entity);
	}

	@Environment(EnvType.CLIENT)
	public static class Client extends UsingEffectiveToolCondition {
		@Override
		protected boolean checkClient(LivingEntity entity) {
			if (entity instanceof ClientPlayerEntity) {
				ClientPlayerInteractionManager interactionMngr = MinecraftClient.getInstance().interactionManager;
				if (interactionMngr.isBreakingBlock())
					return ((PlayerEntity) entity).isUsingEffectiveTool(entity.world.getBlockState(interactionMngr.currentBreakingPos));
			}
			return false;
		}
	}
}
