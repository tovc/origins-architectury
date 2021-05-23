package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.function.Consumer;

public class HealAction implements Consumer<Entity> {
	public static final Codec<HealAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("amount").forGetter(x -> x.amount)
	).apply(instance, HealAction::new));
	private final float amount;

	public HealAction(float amount) {this.amount = amount;}

	@Override
	public void accept(Entity entity) {
		if (entity instanceof LivingEntity) ((LivingEntity) entity).heal(this.amount);
	}
}
