package io.github.apace100.origins.condition.entity;

import io.github.apace100.origins.api.configuration.CodecHelper;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.EntityCondition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class BlockCollisionCondition extends EntityCondition<FieldConfiguration<Vec3d>> {

	public BlockCollisionCondition() {
		super(FieldConfiguration.codec(CodecHelper.vec3d("offset_")));
	}

	@Override
	public boolean check(FieldConfiguration<Vec3d> configuration, LivingEntity entity) {
		Box boundingBox = entity.getBoundingBox();
		boundingBox = boundingBox.offset(configuration.value().multiply(boundingBox.getXLength(), boundingBox.getYLength(), boundingBox.getZLength()));
		return entity.world.getBlockCollisions(entity, boundingBox).findAny().isPresent();
	}
}
