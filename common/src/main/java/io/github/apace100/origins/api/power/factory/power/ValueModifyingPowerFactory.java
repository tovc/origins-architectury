package io.github.apace100.origins.api.power.factory.power;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.IValueModifyingPower;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.configuration.power.IValueModifyingPowerConfiguration;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public abstract class ValueModifyingPowerFactory<T extends IValueModifyingPowerConfiguration> extends PowerFactory<T> implements IValueModifyingPower<T> {
	protected ValueModifyingPowerFactory(Codec<T> codec) {
		super(codec);
	}

	protected ValueModifyingPowerFactory(Codec<T> codec, boolean allowConditions) {
		super(codec, allowConditions);
	}

	@Override
	public List<EntityAttributeModifier> getModifiers(ConfiguredPower<T, ?> configuration, PlayerEntity player) {
		return configuration.getConfiguration().modifiers().getContent();
	}
}
