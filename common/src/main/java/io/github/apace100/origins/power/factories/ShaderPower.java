package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.FieldConfiguration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ShaderPower extends PowerFactory<FieldConfiguration<Identifier>> {

    public ShaderPower() {
        super(FieldConfiguration.codec(Identifier.CODEC, "shader"));
    }
}
