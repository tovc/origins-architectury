package io.github.apace100.origins.power;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.configuration.ListConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModPowers;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

public class EffectImmunityPower extends PowerFactory<ListConfiguration<StatusEffect>> {
	public static boolean isImmune(PlayerEntity player, StatusEffectInstance effect) {
		return OriginComponent.getPowers(player, ModPowers.EFFECT_IMMUNITY.get()).stream().anyMatch(x -> x.getFactory().isImmune(x, player, effect));
	}

	public EffectImmunityPower() {
		super(ListConfiguration.optionalCodec(OriginsCodecs.OPTIONAL_STATUS_EFFECT, "effect", "effects"));
	}

	public boolean isImmune(ConfiguredPower<ListConfiguration<StatusEffect>, ?> configuration, PlayerEntity player, StatusEffect effect) {
		return configuration.getConfiguration().getContent().contains(effect);
	}

	public boolean isImmune(ConfiguredPower<ListConfiguration<StatusEffect>, ?> configuration, PlayerEntity player, StatusEffectInstance effect) {
		return this.isImmune(configuration, player, effect.getEffectType());
	}
}
