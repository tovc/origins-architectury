package io.github.apace100.origins.power;

import io.github.apace100.origins.api.configuration.ListConfiguration;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.util.AttributedEntityAttributeModifier;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;

public class AttributePower extends PowerFactory<ListConfiguration<AttributedEntityAttributeModifier>> {
	public AttributePower() {
		super(ListConfiguration.optionalCodec(OriginsCodecs.OPTIONAL_ATTRIBUTED_ATTRIBUTE_MODIFIER, "modifier", "modifiers"), false);
	}

	@Override
	protected void onAdded(ListConfiguration<AttributedEntityAttributeModifier> configuration, PlayerEntity player) {
		configuration.getContent().stream().filter(x -> player.getAttributes().hasAttribute(x.attribute())).forEach(mod -> {
			EntityAttributeInstance attributeInstance = player.getAttributeInstance(mod.attribute());
			if (!attributeInstance.hasModifier(mod.modifier())) attributeInstance.addTemporaryModifier(mod.modifier());
		});
	}

	@Override
	protected void onRemoved(ListConfiguration<AttributedEntityAttributeModifier> configuration, PlayerEntity player) {
		configuration.getContent().stream().filter(x -> player.getAttributes().hasAttribute(x.attribute())).forEach(mod -> {
			EntityAttributeInstance attributeInstance = player.getAttributeInstance(mod.attribute());
			if (attributeInstance.hasModifier(mod.modifier())) attributeInstance.removeModifier(mod.modifier());
		});
	}
}
