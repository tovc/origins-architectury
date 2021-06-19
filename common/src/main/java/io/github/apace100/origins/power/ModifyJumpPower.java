package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.power.ValueModifyingPowerFactory;
import io.github.apace100.origins.power.configuration.ModifyJumpConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.LivingEntity;

public class ModifyJumpPower extends ValueModifyingPowerFactory<ModifyJumpConfiguration> {
	public static double apply(LivingEntity player, double baseValue) {
		return OriginComponent.modify(player, ModPowers.MODIFY_JUMP.get(), baseValue, x -> true, x -> x.getFactory().execute(x, player));
	}

	public ModifyJumpPower() {
		super(ModifyJumpConfiguration.CODEC);
	}

	public void execute(ConfiguredPower<ModifyJumpConfiguration, ?> configuration, LivingEntity player) {
		ConfiguredEntityAction.execute(configuration.getConfiguration().condition(), player);
	}
}
