package io.github.apace100.origins.power.factories;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.FieldConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.minecraft.entity.Entity;

import java.util.List;

public class InvisibilityPower extends PowerFactory<FieldConfiguration<Boolean>> {

	public static boolean isArmorHidden(Entity player) {
		List<ConfiguredPower<FieldConfiguration<Boolean>, InvisibilityPower>> powers = OriginComponent.getPowers(player, ModPowers.INVISIBILITY.get());
		return !powers.isEmpty() && powers.stream().noneMatch(x -> x.getConfiguration().value());
	}

	public InvisibilityPower() {
		super(FieldConfiguration.codec(Codec.BOOL, "render_armor"));
	}
}
