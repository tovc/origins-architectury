package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import io.github.apace100.origins.util.Space;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.Consumer;

public class AddVelocityAction implements Consumer<Entity> {
	public static final Codec<AddVelocityAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.optionalFieldOf("x", 0F).forGetter(x -> x.x),
			Codec.FLOAT.optionalFieldOf("y", 0F).forGetter(x -> x.y),
			Codec.FLOAT.optionalFieldOf("z", 0F).forGetter(x -> x.z),
			OriginsCodecs.SPACE.optionalFieldOf("space", Space.WORLD).forGetter(x -> x.space),
			Codec.BOOL.optionalFieldOf("set", false).forGetter(x -> x.set)
	).apply(instance, AddVelocityAction::new));

	private final float x;
	private final float y;
	private final float z;
	private final Space space;
	private final boolean set;

	public AddVelocityAction(float x, float y, float z, Space space, boolean set) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.space = space;
		this.set = set;
	}

	@Override
	public void accept(Entity entity) {
		Vector3f vec = new Vector3f(x, y, z);
		TriConsumer<Float, Float, Float> method = set ? entity::setVelocity : entity::addVelocity;
		vec.rotate(space.rotation(entity));
		method.accept(x, y, z);
		entity.velocityModified = true;
	}
}
