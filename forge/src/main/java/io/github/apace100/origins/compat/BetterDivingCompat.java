package io.github.apace100.origins.compat;

import io.github.apace100.origins.power.PowerTypes;
import meldexun.better_diving.api.event.PlayerCanBreathEvent;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraftforge.common.MinecraftForge;

public class BetterDivingCompat {
	public static void registerCompat() {
		MinecraftForge.EVENT_BUS.addListener((PlayerCanBreathEvent event) -> event.setCanBreath(canBreath(event.getPlayer(), event.canBreath())));
	}

	public static boolean canBreath(PlayerEntity player, boolean previous) {
		if (PowerTypes.WATER_BREATHING.isActive(player)) {
			boolean canBreath = player.isSubmergedIn(FluidTags.WATER) || player.isBeingRainedOn();
			canBreath |= player.world.getBlockState(player.getBlockPos()).isOf(Blocks.BUBBLE_COLUMN);
			canBreath |= player.isCreative();
			canBreath |= player.hasStatusEffect(StatusEffects.WATER_BREATHING) || player.hasStatusEffect(StatusEffects.CONDUIT_POWER);
			return canBreath;
		}
		return previous;
	}
}
