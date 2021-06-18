package io.github.apace100.origins.power.configuration.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import net.minecraft.entity.Entity;

import java.util.Optional;

public record ColorConfiguration(float red, float green, float blue, float alpha) implements IOriginsFeatureConfiguration {
	public static Optional<ColorConfiguration> forPower(Entity entity, PowerFactory<ColorConfiguration> factory) {
		return OriginComponent.getPowers(entity, factory).stream().map(ConfiguredFactory::getConfiguration).reduce(ColorConfiguration::merge);
	}

	public static final Codec<ColorConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.optionalFieldOf("red", 1.0F).forGetter(ColorConfiguration::red),
			Codec.FLOAT.optionalFieldOf("green", 1.0F).forGetter(ColorConfiguration::green),
			Codec.FLOAT.optionalFieldOf("blue", 1.0F).forGetter(ColorConfiguration::blue),
			Codec.FLOAT.optionalFieldOf("alpha", 1.0F).forGetter(ColorConfiguration::alpha)
	).apply(instance, ColorConfiguration::new));

	public ColorConfiguration merge(ColorConfiguration other) {
		return new ColorConfiguration(this.red() * other.red(), this.green() * other.green(), this.blue() * other.blue(), Math.min(this.alpha(), other.alpha()));
	}
}
