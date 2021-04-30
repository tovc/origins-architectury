package io.github.apace100.origins.access;

import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;

public class EntityShapeContextAccessor {
	@ExpectPlatform
	public static Entity getEntity(EntityShapeContext context) {
		throw new AssertionError();
	}
}
