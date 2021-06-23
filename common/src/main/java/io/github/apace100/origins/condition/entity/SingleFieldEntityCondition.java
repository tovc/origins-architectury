package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.MapCodec;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.function.BiPredicate;

public class SingleFieldEntityCondition<T> extends EntityCondition<FieldConfiguration<T>> {
	public static boolean checkPredicate(LivingEntity entity, Identifier identifier) {
		MinecraftServer server = entity.world.getServer();
		if (server != null) {
			LootCondition lootCondition = server.getPredicateManager().get(identifier);
			if (lootCondition != null) {
				LootContext.Builder lootBuilder = (new LootContext.Builder((ServerWorld) entity.world))
						.parameter(LootContextParameters.ORIGIN, entity.getPos())
						.optionalParameter(LootContextParameters.THIS_ENTITY, entity);
				return lootCondition.test(lootBuilder.build(LootContextTypes.COMMAND));
			}
		}
		return false;
	}
	private final BiPredicate<LivingEntity, T> predicate;

	public SingleFieldEntityCondition(MapCodec<T> codec, BiPredicate<LivingEntity, T> predicate) {
		super(FieldConfiguration.codec(codec));
		this.predicate = predicate;
	}

	@Override
	public boolean check(FieldConfiguration<T> configuration, LivingEntity entity) {
		return this.predicate.test(entity, configuration.value());
	}
}
