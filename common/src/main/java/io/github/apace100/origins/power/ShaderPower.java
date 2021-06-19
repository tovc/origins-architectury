package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.api.power.configuration.power.FieldConfiguration;
import net.minecraft.util.Identifier;

public class ShaderPower extends PowerFactory<FieldConfiguration<Identifier>> {

    public ShaderPower() {
        super(FieldConfiguration.codec(Identifier.CODEC, "shader"));
    }
}
