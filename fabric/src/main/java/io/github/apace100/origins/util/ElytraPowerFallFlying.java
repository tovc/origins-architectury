package io.github.apace100.origins.util;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.registry.ModPowers;
import net.adriantodt.fallflyinglib.FallFlyingAbility;
import net.minecraft.entity.LivingEntity;

public class ElytraPowerFallFlying implements FallFlyingAbility {

    private final LivingEntity entity;

    public ElytraPowerFallFlying(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean allowFallFlying() {
        return OriginComponent.hasPower(entity, ModPowers.ELYTRA_FLIGHT.get());
    }

    @Override
    public boolean shouldHideCape() {
        //TODO Elytra render features can mostly be moved here.
        return false;
    }
}
