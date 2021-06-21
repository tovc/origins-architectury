package io.github.apace100.origins.power;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.INightVisionPower;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import net.minecraft.entity.player.PlayerEntity;

public class NightVisionPower extends PowerFactory<FieldConfiguration<Float>> implements INightVisionPower<FieldConfiguration<Float>> {
	public NightVisionPower() {
		super(FieldConfiguration.codec(Codec.FLOAT, "strength", 1.0F));
	}

	@Override
	public float getStrength(ConfiguredPower<FieldConfiguration<Float>, ?> configuration, PlayerEntity player) {
		return configuration.getConfiguration().value();
	}
}
