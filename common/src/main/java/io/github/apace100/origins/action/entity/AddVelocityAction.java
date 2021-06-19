package io.github.apace100.origins.action.entity;

import io.github.apace100.origins.action.configuration.AddVelocityConfiguration;
import io.github.apace100.origins.api.power.factory.EntityAction;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import org.apache.logging.log4j.util.TriConsumer;

public class AddVelocityAction extends EntityAction<AddVelocityConfiguration> {
	public AddVelocityAction() {
		super(AddVelocityConfiguration.CODEC);
	}

	@Override
	public void execute(AddVelocityConfiguration configuration, Entity entity) {
		Vector3f vec = configuration.getVector();
		TriConsumer<Float, Float, Float> method = configuration.set() ? entity::setVelocity : entity::addVelocity;
		vec.rotate(configuration.space().rotation(entity));
		method.accept(configuration.x(), configuration.y(), configuration.z());
		entity.velocityModified = true;
	}
}
