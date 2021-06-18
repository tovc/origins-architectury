package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.FieldConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;

public class WalkOnFluidPower extends PowerFactory<FieldConfiguration<Tag<Fluid>>> {

    public WalkOnFluidPower() {
        super(FieldConfiguration.codec(OriginsCodecs.FLUID_TAG, "fluid"));
    }
}
