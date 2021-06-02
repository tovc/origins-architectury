package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Consumer;

public class AddXPAction implements Consumer<Entity> {

	public static final Codec<AddXPAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("points", 0).forGetter(x -> x.points),
			Codec.INT.optionalFieldOf("levels", 0).forGetter(x -> x.levels)
	).apply(instance, AddXPAction::new));

	private final int points;
	private final int levels;

	public AddXPAction(int points, int levels) {
		this.points = points;
		this.levels = levels;
	}

	@Override
	public void accept(Entity entity) {
		if(entity instanceof PlayerEntity) {
			if(points > 0)
				((PlayerEntity)entity).addExperience(points);
			((PlayerEntity)entity).addExperienceLevels(levels);
		}
	}
}
