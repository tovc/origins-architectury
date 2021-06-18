package io.github.apace100.origins.power.factories;

import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.FieldConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.player.PlayerEntity;

public class EntityGroupPower extends PowerFactory<FieldConfiguration<EntityGroup>> {

    public EntityGroupPower() {
        super(FieldConfiguration.codec(OriginsCodecs.ENTITY_GROUP, "group"));
    }
}
