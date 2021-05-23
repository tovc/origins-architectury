package io.github.apace100.origins.util;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

import static net.minecraft.util.math.MathHelper.cos;
import static net.minecraft.util.math.MathHelper.sin;

public enum Space {
	WORLD(x -> Quaternion.IDENTITY),
	LOCAL(x -> make(x.pitch, x.yaw, 0, true)),
	LOCAL_HORIZONTAL(x -> make(x.pitch, 0, 0, true)),
	VELOCITY(x -> rotation(gf(), x.getVelocity())),
	VELOCITY_NORMALIZED(x -> rotation(gf(), x.getVelocity().normalize())),
	VELOCITY_HORIZONTAL(x -> rotation(gf(), x.getVelocity().subtract(0, x.getVelocity().y, 0))),
	VELOCITY_HORIZONTAL_NORMALIZED(x -> rotation(gf(), x.getVelocity().subtract(0, x.getVelocity().y, 0).normalize()));

	private static final Vec3d GLOBAL_FORWARD = new Vec3d(0, 0, 1);

	private static Vec3d gf() { return GLOBAL_FORWARD; }

	private static Quaternion rotation(Vec3d absolute, Vec3d rotated) {
		Vec3d vec3d = absolute.crossProduct(rotated);
		Quaternion quaternion = new Quaternion(
				(float) vec3d.x, (float) vec3d.y, (float) vec3d.z,
				(float) (Math.sqrt(absolute.lengthSquared() + rotated.lengthSquared()) + absolute.dotProduct(rotated))
		);
		quaternion.normalize();
		return quaternion;
	}

	private static Quaternion make(float pitch, float yaw, float roll, boolean bl) {
		if (bl) {
			pitch *= 0.017453292F;
			yaw *= 0.017453292F;
			roll *= 0.017453292F;
		}

		float i = sin(0.5F * pitch);
		float j = cos(0.5F * pitch);
		float k = sin(0.5F * yaw);
		float l = cos(0.5F * yaw);
		float m = sin(0.5F * roll);
		float n = cos(0.5F * roll);
		return new Quaternion(
				i * l * n + j * k * m,
				j * k * n - i * l * m,
				i * k * n + j * l * m,
				j * l * n - i * k * m);
	}

	public static void rotateVectorToBase(Vec3d newBase, Vector3f vector) {
		Vec3d globalForward = new Vec3d(0, 0, 1);
		Vec3d v = globalForward.crossProduct(newBase).normalize();
		double c = Math.acos(globalForward.dotProduct(newBase));
		Quaternion quat = new Quaternion(new Vector3f((float) v.x, (float) v.y, (float) v.z), (float) c, false);
		vector.rotate(quat);
	}

	private final Function<Entity, Quaternion> function;

	Space(Function<Entity, Quaternion> function) {
		this.function = function;
	}

	public Quaternion rotation(Entity entity) {
		return this.function.apply(entity);
	}
}
