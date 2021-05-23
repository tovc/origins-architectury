package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;

import java.util.function.Consumer;

public class SetOnFireAction implements Consumer<Entity> {
	public static final Codec<SetOnFireAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("duration").forGetter(x -> x.duration)
	).apply(instance, SetOnFireAction::new));

	private final int duration;

	public SetOnFireAction(int duration) {this.duration = duration;}

	@Override
	public void accept(Entity entity) {
		entity.setOnFireFor(duration);
	}
}
