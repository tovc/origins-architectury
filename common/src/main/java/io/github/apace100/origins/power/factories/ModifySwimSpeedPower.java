package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.power.factory.power.AttributeModifyingPowerFactory;
import io.github.apace100.origins.power.configuration.power.ValueModifyingPowerConfiguration;
import io.github.apace100.origins.registry.ModRegistriesArchitectury;
import net.minecraft.entity.attribute.EntityAttribute;
import org.jetbrains.annotations.Nullable;

public class ModifySwimSpeedPower extends AttributeModifyingPowerFactory<ValueModifyingPowerConfiguration> {

	public ModifySwimSpeedPower() {
		super(ValueModifyingPowerConfiguration.CODEC);
	}

	@Override
	public @Nullable EntityAttribute getAttribute() {
		//Forge: ForgeMod#SWIM_SPEED_ATTRIBUTE
		//Fabric: null unless defined by another mod.
		return ModRegistriesArchitectury.SWIM_SPEED.getOrNull();
	}
}
