package io.github.apace100.origins.power.factories;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.FieldConfiguration;

/**
 * @deprecated Unused in the original code of origins, not registered anywhere, so I'm skipping for now.
 */
@Deprecated
public class FloatPower extends PowerFactory<FieldConfiguration<Float>> {

	public FloatPower() {
		super(FieldConfiguration.codec(Codec.FLOAT, "value"));
	}
}
