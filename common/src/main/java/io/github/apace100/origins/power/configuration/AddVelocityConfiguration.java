package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import io.github.apace100.origins.util.Space;
import net.minecraft.client.util.math.Vector3f;

public record AddVelocityConfiguration(float x, float y, float z, Space space,
									   boolean set) implements IOriginsFeatureConfiguration {
	public static final Codec<AddVelocityConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.optionalFieldOf("x", 0F).forGetter(AddVelocityConfiguration::x),
			Codec.FLOAT.optionalFieldOf("y", 0F).forGetter(AddVelocityConfiguration::y),
			Codec.FLOAT.optionalFieldOf("z", 0F).forGetter(AddVelocityConfiguration::z),
			OriginsCodecs.SPACE.optionalFieldOf("space", Space.WORLD).forGetter(AddVelocityConfiguration::space),
			Codec.BOOL.optionalFieldOf("set", false).forGetter(AddVelocityConfiguration::set)
	).apply(instance, AddVelocityConfiguration::new));

	public Vector3f getVector() {
		return new Vector3f(x, y, z);
	}
}
