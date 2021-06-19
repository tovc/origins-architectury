package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.StackingStatusEffectConfiguration;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

import java.util.concurrent.atomic.AtomicInteger;

public class StackingStatusEffectPower extends PowerFactory<StackingStatusEffectConfiguration> {
	public StackingStatusEffectPower() {
		super(StackingStatusEffectConfiguration.CODEC);
		this.ticking(true);
	}

	public AtomicInteger getCurrentStacks(ConfiguredPower<StackingStatusEffectConfiguration, ?> configuration, PlayerEntity player) {
		return configuration.getPowerData(player, () -> new AtomicInteger(0));
	}

	@Override
	public void tick(ConfiguredPower<StackingStatusEffectConfiguration, ?> configuration, PlayerEntity player) {
		AtomicInteger currentStacks = getCurrentStacks(configuration, player);
		StackingStatusEffectConfiguration config = configuration.getConfiguration();
		if (configuration.isActive(player)) {
			int i = currentStacks.addAndGet(1);
			if (i > config.max())
				currentStacks.set(config.max());
			if (i > 0)
				this.applyEffects(configuration, player);
		} else {
			int i = currentStacks.addAndGet(-1);
			if (i < config.min())
				currentStacks.set(config.min());
		}
	}

	@Override
	protected int tickInterval(StackingStatusEffectConfiguration configuration, PlayerEntity player) {
		return 10;
	}

	@Override
	public Tag serialize(ConfiguredPower<StackingStatusEffectConfiguration, ?> configuration, PlayerEntity player) {
		return IntTag.of(this.getCurrentStacks(configuration, player).get());
	}

	@Override
	public void deserialize(ConfiguredPower<StackingStatusEffectConfiguration, ?> configuration, PlayerEntity player, Tag tag) {
		if (tag instanceof IntTag intTag)
			this.getCurrentStacks(configuration, player).set(intTag.getInt());
	}

	public void applyEffects(ConfiguredPower<StackingStatusEffectConfiguration, ?> configuration, PlayerEntity player) {
		configuration.getConfiguration().effects().getContent().forEach(sei -> {
			int duration = configuration.getConfiguration().duration() * this.getCurrentStacks(configuration, player).get();
			if (duration > 0) {
				StatusEffectInstance applySei = new StatusEffectInstance(sei.getEffectType(), duration, sei.getAmplifier(), sei.isAmbient(), sei.shouldShowParticles(), sei.shouldShowIcon());
				player.addStatusEffect(applySei);
			}
		});
	}
}
