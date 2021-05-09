package io.github.apace100.origins.power.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;

import java.util.function.Predicate;

public class BlockCollisionCondition implements Predicate<LivingEntity> {

	public static final Codec<BlockCollisionCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("offset_x").forGetter(x -> x.offsetX),
			Codec.FLOAT.fieldOf("offset_y").forGetter(x -> x.offsetY),
			Codec.FLOAT.fieldOf("offset_z").forGetter(x -> x.offsetZ)
	).apply(instance, BlockCollisionCondition::new));

	private final float offsetX;
	private final float offsetY;
	private final float offsetZ;

	public BlockCollisionCondition(float offsetX, float offsetY, float offsetZ) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
	}

	@Override
	public boolean test(LivingEntity entity) {
		return entity.world.getBlockCollisions(entity,
				entity.getBoundingBox().offset(
						offsetX * entity.getBoundingBox().getXLength(),
						offsetY * entity.getBoundingBox().getYLength(),
						offsetZ * entity.getBoundingBox().getZLength())
		).findAny().isPresent();
	}
}
