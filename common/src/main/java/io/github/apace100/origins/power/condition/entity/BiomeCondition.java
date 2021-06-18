package io.github.apace100.origins.power.condition.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//FIXME This is wrong, it should be using RegistryKey instead.
public class BiomeCondition implements Predicate<LivingEntity> {

	public static final Codec<BiomeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_BIOME.optionalFieldOf("biome", Optional.empty()).forGetter(BiomeCondition::getSingularBiome),
			OriginsCodecs.listOf(OriginsCodecs.OPTIONAL_BIOME).optionalFieldOf("biomes", ImmutableList.of()).forGetter(BiomeCondition::getMultipleBiomes),
			OriginsCodecs.BIOME_CONDITION.optionalFieldOf("condition").forGetter(x -> x.biomeCondition)
	).apply(instance, BiomeCondition::new));

	private final Set<Biome> biomes;
	private final Optional<ConditionFactory.Instance<Biome>> biomeCondition;

	public BiomeCondition(Optional<Biome> biome, List<Optional<Biome>> biomes, Optional<ConditionFactory.Instance<Biome>> biomeCondition) {
		this.biomeCondition = biomeCondition;
		ImmutableSet.Builder<Biome> builder = ImmutableSet.<Biome>builder().addAll(biomes.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
		biome.ifPresent(builder::add);
		this.biomes = builder.build();
	}

	private Optional<Biome> getSingularBiome() {
		return this.biomes.size() == 1 ? this.biomes.stream().findFirst() : Optional.empty();
	}

	private List<Optional<Biome>> getMultipleBiomes() {
		return this.biomes.size() == 1 ? ImmutableList.of() : this.biomes.stream().map(Optional::of).collect(ImmutableList.toImmutableList());
	}

	@Override
	public boolean test(LivingEntity entity) {
		Biome biome = entity.world.getBiome(entity.getBlockPos());
		boolean cond = this.biomeCondition.map(x -> x.test(biome)).orElse(true);
		if (!cond)
			return false;
		return biomes.size() <= 0 || biomes.stream().anyMatch(x -> Objects.equals(biome, x));
	}
}
