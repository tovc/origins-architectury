package io.github.apace100.origins.api.origin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

public record OriginUpgrade(Identifier advancementCondition, Identifier upgradeToOrigin, String announcement) {

    public static final Codec<OriginUpgrade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("condition").forGetter(OriginUpgrade::advancementCondition),
            Identifier.CODEC.fieldOf("origin").forGetter(OriginUpgrade::upgradeToOrigin),
            Codec.STRING.optionalFieldOf("announcement", "").forGetter(OriginUpgrade::announcement)
    ).apply(instance, OriginUpgrade::new));


}
