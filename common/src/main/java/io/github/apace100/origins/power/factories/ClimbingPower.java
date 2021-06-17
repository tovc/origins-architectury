package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.power.ClimbingConfiguration;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.function.Consumer;

public class ClimbingPower extends PowerFactory<ClimbingConfiguration> {
	public static boolean check(PlayerEntity player, Consumer<BlockPos> climbingPosSetter) {
		List<ConfiguredPower<ClimbingConfiguration, ClimbingPower>> climbingPowers = ModComponentsArchitectury.getOriginComponent(player).getPowers(ModPowers.CLIMBING.get());
		if (climbingPowers.size() > 0) {
			if (climbingPowers.stream().anyMatch(x -> x.isActive(player))) {
				climbingPosSetter.accept(player.getBlockPos());
				return true;
			}
			return player.isHoldingOntoLadder() && climbingPowers.stream().anyMatch(x -> x.getFactory().canHold(x, player));
		}
		return false;
	}

	public ClimbingPower() {
		super(ClimbingConfiguration.CODEC);
	}

	public boolean canHold(ConfiguredPower<ClimbingConfiguration, ?> configuration, PlayerEntity player) {
		ConfiguredEntityCondition<?, ?> holdingCondition = configuration.getConfiguration().condition();
		return configuration.getConfiguration().allowHolding() && (holdingCondition == null ? configuration.isActive(player) : holdingCondition.check(player));
	}
}
