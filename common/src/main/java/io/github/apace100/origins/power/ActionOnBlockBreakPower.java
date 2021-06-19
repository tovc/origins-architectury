package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.ActionOnBlockBreakConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ActionOnBlockBreakPower extends PowerFactory<ActionOnBlockBreakConfiguration> {
	public static void execute(PlayerEntity player, CachedBlockPosition pos, boolean successful) {
		OriginComponent.getPowers(player, ModPowers.ACTION_ON_BLOCK_BREAK.get()).stream()
				.filter(p -> p.getFactory().doesApply(p, player, pos))
				.forEach(aobbp -> aobbp.getFactory().executeActions(aobbp, player, successful, pos.getBlockPos(), null));
	}

	public ActionOnBlockBreakPower() {
		super(ActionOnBlockBreakConfiguration.CODEC);
	}

	public boolean doesApply(ConfiguredFactory<ActionOnBlockBreakConfiguration, ?> config, PlayerEntity player, BlockPos pos) {
		return this.doesApply(config, player, new CachedBlockPosition(player.world, pos, true));
	}

	public boolean doesApply(ConfiguredFactory<ActionOnBlockBreakConfiguration, ?> config, PlayerEntity player, CachedBlockPosition cbp) {
		return config.getConfiguration().blockCondition() == null || config.getConfiguration().blockCondition().check(cbp);
	}

	public void executeActions(ConfiguredFactory<ActionOnBlockBreakConfiguration, ?> config, PlayerEntity player, boolean successfulHarvest, BlockPos pos, Direction dir) {
		ActionOnBlockBreakConfiguration configuration = config.getConfiguration();
		if (successfulHarvest || !configuration.onlyWhenHarvested()) {
			if (configuration.blockAction() != null) {
				configuration.blockAction().execute(player.world, pos, dir);
			}
			if (configuration.entityAction() != null) {
				configuration.entityAction().execute(player);
			}
		}
	}
}
