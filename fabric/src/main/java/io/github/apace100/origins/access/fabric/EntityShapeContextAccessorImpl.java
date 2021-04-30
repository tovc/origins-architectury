package io.github.apace100.origins.access.fabric;

import io.github.apace100.origins.access.EntityShapeContextAccess;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;

public class EntityShapeContextAccessorImpl {
	public static Entity getEntity(EntityShapeContext context) {
		return ((EntityShapeContextAccess) context).getEntity();
	}
}
