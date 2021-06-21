package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class AdvancementCondition extends EntityCondition<FieldConfiguration<Identifier>> {

	public AdvancementCondition() {
		super(FieldConfiguration.codec(Identifier.CODEC, "builder"));
	}

	protected boolean testClient(LivingEntity entity) {
		return false;
	}

	@Override
	public boolean check(FieldConfiguration<Identifier> configuration, LivingEntity entity) {
		if (entity instanceof ServerPlayerEntity) {
			Advancement advancement = entity.getServer().getAdvancementLoader().get(configuration.value());
			if (advancement == null)
				Origins.LOGGER.warn("Advancement \"{}\" did not exist, but was referenced in an \"origins:advancement\" condition.", configuration.value().toString());
			else
				return ((ServerPlayerEntity) entity).getAdvancementTracker().getProgress(advancement).isDone();
		}
		return testClient(entity);
	}

	@Environment(EnvType.CLIENT)
	public static class Client extends AdvancementCondition {
		public Client() {
			super();
		}

		@Override
		protected boolean testClient(LivingEntity entity) {
			if (entity instanceof ClientPlayerEntity) {
				ClientAdvancementManager advancementManager = MinecraftClient.getInstance().getNetworkHandler().getAdvancementHandler();
				Advancement advancement = advancementManager.getManager().get(this.advancement);
				if (advancement != null) {
					Map<Advancement, AdvancementProgress> progressMap = advancementManager.advancementProgresses;
					if (progressMap.containsKey(advancement))
						return progressMap.get(advancement).isDone();
				}
				// We don't want to print an error here if the advancement does not exist,
				// because on the client-side the advancement could just not have been received from the server.
			}
			return false;
		}
	}
}
