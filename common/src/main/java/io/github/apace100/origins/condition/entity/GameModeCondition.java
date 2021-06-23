package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import io.github.apace100.origins.util.OriginsCodecs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;

import java.util.Objects;

public class GameModeCondition extends EntityCondition<FieldConfiguration<GameMode>> {

	public GameModeCondition() {
		super(FieldConfiguration.codec(OriginsCodecs.GAME_MODE, "gamemode"));
	}

	protected boolean testClient(GameMode mode, LivingEntity entity) {
		return false;
	}

	@Override
	public boolean check(FieldConfiguration<GameMode> configuration, LivingEntity entity) {
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerInteractionManager interactionMngr = ((ServerPlayerEntity) entity).interactionManager;
			return Objects.equals(interactionMngr.getGameMode(), configuration.value());
		}
		return testClient(configuration.value(), entity);
	}

	@Environment(EnvType.CLIENT)
	public static class Client extends GameModeCondition {
		public Client() {
			super();
		}

		@Override
		protected boolean testClient(GameMode mode, LivingEntity entity) {
			return entity instanceof ClientPlayerEntity && Objects.equals(MinecraftClient.getInstance().interactionManager.getCurrentGameMode(), mode);
		}
	}
}
