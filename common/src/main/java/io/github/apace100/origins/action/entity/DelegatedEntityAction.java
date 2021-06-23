package io.github.apace100.origins.action.entity;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.action.meta.IDelegatedActionConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.entity.Entity;

public class DelegatedEntityAction<T extends IDelegatedActionConfiguration<Entity>> extends EntityAction<T> {
	public DelegatedEntityAction(Codec<T> codec) {
		super(codec);
	}

	@Override
	public void execute(T configuration, Entity entity) {
		configuration.execute(entity);
	}
}
