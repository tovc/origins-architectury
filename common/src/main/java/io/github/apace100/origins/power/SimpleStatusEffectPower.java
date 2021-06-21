package io.github.apace100.origins.power;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.api.configuration.ListConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @deprecated Unused in origins.
 */
@Deprecated
public class SimpleStatusEffectPower extends PowerFactory<ListConfiguration<StatusEffectInstance>> {
	public SimpleStatusEffectPower(Codec<ListConfiguration<StatusEffectInstance>> codec) {
		super(ListConfiguration.codec(OriginsCodecs.STATUS_EFFECT_INSTANCE, "effect", "effects"));
		this.ticking();
	}

	@Override
	protected void tick(ListConfiguration<StatusEffectInstance> configuration, PlayerEntity player) {
		configuration.getContent().forEach(sei -> player.applyStatusEffect(new StatusEffectInstance(sei)));
	}

	@Override
	public int tickInterval(ConfiguredPower<ListConfiguration<StatusEffectInstance>, ?> configuration, PlayerEntity player) {
		return 10;
	}
}
