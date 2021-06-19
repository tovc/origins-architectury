package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import java.util.function.Function;
import java.util.function.Predicate;

public class GameModeCondition implements Predicate<LivingEntity> {
	public static Codec<GameModeCondition> codec(Function<GameMode, ? extends GameModeCondition> builder) {
		return RecordCodecBuilder.create(instance -> instance.group(
				OriginsCodecs.GAME_MODE.fieldOf("gamemode").forGetter(x -> x.gamemode)
		).apply(instance, builder::apply));
	}

	protected final GameMode gamemode;

	public GameModeCondition(GameMode gamemode) {this.gamemode = gamemode;}

	protected boolean testClient(LivingEntity entity) {
		return false;
	}

	@Override
	public boolean test(LivingEntity entity) {
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerInteractionManager interactionMngr = ((ServerPlayerEntity) entity).interactionManager;
			return Objects.equals(interactionMngr.getGameMode(), this.gamemode);
		}
		return testClient(entity);
	}

	@Environment(EnvType.CLIENT)
	public static class Client extends GameModeCondition {
		public Client(GameMode gamemode) {
			super(gamemode);
		}

		@Override
		protected boolean testClient(LivingEntity entity) {
			return entity instanceof ClientPlayerEntity && Objects.equals(MinecraftClient.getInstance().interactionManager.getCurrentGameMode(), this.gamemode);
		}
	}
}
