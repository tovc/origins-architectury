package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Consumer;

public class FeedAction implements Consumer<Entity> {

	public static Codec<FeedAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("food").forGetter(x -> x.food),
			Codec.FLOAT.fieldOf("saturation").forGetter(x -> x.saturation)
	).apply(instance, FeedAction::new));

	public final int food;
	public final float saturation;

	public FeedAction(int food, float saturation) {
		this.food = food;
		this.saturation = saturation;
	}

	@Override
	public void accept(Entity entity) {
		if(entity instanceof PlayerEntity) {
			((PlayerEntity)entity).getHungerManager().add(food, saturation);
		}
	}
}
