package io.github.apace100.origins.power.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.function.Predicate;

public class DimensionCondition implements Predicate<LivingEntity> {

	public static final Codec<DimensionCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.DIMENSION.fieldOf("dimension").forGetter(x -> x.dimension)
	).apply(instance, DimensionCondition::new));

	private final RegistryKey<World> dimension;

	public DimensionCondition(RegistryKey<World> dimension) {this.dimension = dimension;}

	@Override
	public boolean test(LivingEntity entity) {
		return Objects.equals(entity.world.getRegistryKey(), this.dimension);
	}
}
