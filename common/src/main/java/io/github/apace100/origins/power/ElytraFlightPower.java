package io.github.apace100.origins.power;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.entity.player.PlayerEntity;

public class ElytraFlightPower extends Power {

	@ExpectPlatform
	public static void enableFlight(PlayerEntity player) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void disableFlight(PlayerEntity player) {
		throw new AssertionError();
	}
	private final boolean renderElytra;

	public ElytraFlightPower(PowerType<?> type, PlayerEntity player, boolean renderElytra) {
		super(type, player);
		this.renderElytra = renderElytra;
		this.setTicking(true);
	}

	public boolean shouldRenderElytra() {
		return renderElytra;
	}

	@Override
	public void tick() {
		if (this.isActive())
			enableFlight(player);
		else
			disableFlight(player);
	}

	@Override
	public void onRemoved() {
		disableFlight(player);
	}
}
