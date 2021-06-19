package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.PreventSleepConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PreventSleepPower extends PowerFactory<PreventSleepConfiguration> {

	public static boolean tryPreventSleep(PlayerEntity player, World world, BlockPos pos) {
		CachedBlockPosition cbp = new CachedBlockPosition(world, pos, true);
		boolean flag = false;
		for (ConfiguredPower<PreventSleepConfiguration, PreventSleepPower> p : OriginComponent.getPowers(player, ModPowers.PREVENT_SLEEP.get())) {
			if (p.getFactory().doesPrevent(p, cbp)) {
				if (p.getConfiguration().allowSpawn() && player instanceof ServerPlayerEntity spe)
					spe.setSpawnPoint(world.getRegistryKey(), pos, spe.yaw, false, true);
				player.sendMessage(new TranslatableText(p.getConfiguration().message()), true);
				flag = true;
			}
		}
		return flag;
	}

	public PreventSleepPower() {
		super(PreventSleepConfiguration.CODEC);
	}

	public boolean doesPrevent(ConfiguredPower<PreventSleepConfiguration, ?> configuration, CachedBlockPosition cbp) {
		return ConfiguredBlockCondition.check(configuration.getConfiguration().condition(), cbp);
	}
}
