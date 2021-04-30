package io.github.apace100.origins.access.forge;

import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;

public class EntityShapeContextAccessorImpl {
	public static Entity getEntity(EntityShapeContext context) {
		return context.getEntity();
	}
}
