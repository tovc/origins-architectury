package io.github.apace100.origins.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.function.Predicate;

public class PredicateCondition implements Predicate<LivingEntity> {

	public static final Codec<PredicateCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("predicate").forGetter(x -> x.predicate)
	).apply(instance, PredicateCondition::new));

	private final Identifier predicate;

	public PredicateCondition(Identifier predicate) {this.predicate = predicate;}

	@Override
	public boolean test(LivingEntity entity) {
		MinecraftServer server = entity.world.getServer();
		if (server != null) {
			LootCondition lootCondition = server.getPredicateManager().get(predicate);
			if (lootCondition != null) {
				LootContext.Builder lootBuilder = (new LootContext.Builder((ServerWorld) entity.world))
						.parameter(LootContextParameters.ORIGIN, entity.getPos())
						.optionalParameter(LootContextParameters.THIS_ENTITY, entity);
				return lootCondition.test(lootBuilder.build(LootContextTypes.COMMAND));
			}
		}
		return false;
	}
}
