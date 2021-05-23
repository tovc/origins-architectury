package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Consumer;

public class ExhaustAction implements Consumer<Entity> {
	public static final Codec<ExhaustAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("amount").forGetter(x -> x.amount)
	).apply(instance, ExhaustAction::new));
	private final float amount;

	public ExhaustAction(float amount) {this.amount = amount;}

	@Override
	public void accept(Entity entity) {
		if (entity instanceof PlayerEntity) ((PlayerEntity) entity).addExhaustion(this.amount);
	}
}
