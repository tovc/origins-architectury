package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.api.power.configuration.power.ListConfiguration;
import io.github.apace100.origins.power.configuration.ConditionedAttributeConfiguration;
import io.github.apace100.origins.util.AttributedEntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;

public class ConditionedAttributePower extends PowerFactory<ConditionedAttributeConfiguration> {
	public ConditionedAttributePower() {
		super(ConditionedAttributeConfiguration.CODEC);
		this.ticking(true);
	}

	private void add(ListConfiguration<AttributedEntityAttributeModifier> configuration, PlayerEntity player) {
		configuration.getContent().stream().filter(x -> player.getAttributes().hasAttribute(x.attribute())).forEach(mod -> {
			EntityAttributeInstance attributeInstance = player.getAttributeInstance(mod.attribute());
			if (!attributeInstance.hasModifier(mod.modifier())) attributeInstance.addTemporaryModifier(mod.modifier());
		});
	}


	private void remove(ListConfiguration<AttributedEntityAttributeModifier> configuration, PlayerEntity player) {
		configuration.getContent().stream().filter(x -> player.getAttributes().hasAttribute(x.attribute())).forEach(mod -> {
			EntityAttributeInstance attributeInstance = player.getAttributeInstance(mod.attribute());
			if (attributeInstance.hasModifier(mod.modifier())) attributeInstance.removeModifier(mod.modifier());
		});
	}

	@Override
	public void tick(ConfiguredPower<ConditionedAttributeConfiguration, ?> configuration, PlayerEntity player) {
		if (configuration.isActive(player))
			this.add(configuration.getConfiguration().modifiers(), player);
		else
			this.remove(configuration.getConfiguration().modifiers(), player);
	}

	@Override
	protected void onRemoved(ConditionedAttributeConfiguration configuration, PlayerEntity player) {
		this.remove(configuration.modifiers(), player);
	}

	@Override
	protected int tickInterval(ConditionedAttributeConfiguration configuration, PlayerEntity player) {
		return configuration.tickRate();
	}
}
