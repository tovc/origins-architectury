package io.github.apace100.origins.power;

import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.EntityGroup;

public class EntityGroupPower extends PowerFactory<FieldConfiguration<EntityGroup>> {

    public EntityGroupPower() {
        super(FieldConfiguration.codec(OriginsCodecs.ENTITY_GROUP, "group"));
    }
}
