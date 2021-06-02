package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;

import java.util.function.Consumer;

public class SetFallDistanceAction implements Consumer<Entity> {
	public static final Codec<SetFallDistanceAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("fall_distance").forGetter(x -> x.fallDistance)
	).apply(instance, SetFallDistanceAction::new));

	private final float fallDistance;

	public SetFallDistanceAction(float fallDistance) {this.fallDistance = fallDistance;}

	@Override
	public void accept(Entity entity) {
		entity.fallDistance = fallDistance;
	}
}
