package io.github.apace100.origins.power;

import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.player.PlayerEntity;

public class ElytraFlightPower extends PowerFactory<FieldConfiguration<Boolean>> {
	public static boolean shouldRenderElytra(PlayerEntity player) {
		return OriginComponent.getPowers(player, ModPowers.ELYTRA_FLIGHT.get()).stream().anyMatch(x -> x.getConfiguration().value());
	}

	@ExpectPlatform
	public static void enableFlight(PlayerEntity player) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void disableFlight(PlayerEntity player) {
		throw new AssertionError();
	}

	public ElytraFlightPower() {
		super(FieldConfiguration.codec(Codec.BOOL, "render_elytra"));
		this.ticking(true);
	}

	@Override
	public void tick(ConfiguredPower<FieldConfiguration<Boolean>, ?> configuration, PlayerEntity player) {
		if (configuration.isActive(player))
			enableFlight(player);
		else
			disableFlight(player);
	}
}
