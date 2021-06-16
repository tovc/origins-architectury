package io.github.apace100.origins.api.origin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public record ConditionedOrigin(List<Identifier> origins, @Nullable ConfiguredEntityCondition<?> condition) {
	public static final Codec<ConditionedOrigin> PURE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.listOf(Identifier.CODEC).fieldOf("conditionedOrigins").forGetter(ConditionedOrigin::origins),
			ConfiguredEntityCondition.CODEC.fieldOf("condition").forGetter(ConditionedOrigin::condition)
	).apply(instance, (origins, condition) -> new ConditionedOrigin(ImmutableList.copyOf(origins), condition)));
	public static final Codec<ConditionedOrigin> CODEC = Codec.either(Identifier.CODEC, PURE_CODEC).xmap(
			either -> either.map(ConditionedOrigin::new, Function.identity()),
			origin -> origin.origins().size() == 1 && origin.condition() == null ? Either.left(origin.origins().get(0)) : Either.right(origin)
	);

	public ConditionedOrigin(Identifier... identifiers) {
		this(ImmutableList.copyOf(identifiers), null);
	}

	public ConditionedOrigin(ConfiguredEntityCondition<?> condition, Identifier... identifiers) {
		this(ImmutableList.copyOf(identifiers), condition);
	}

	public boolean check(LivingEntity entity) {
		return this.condition() == null || this.condition().check(entity);
	}
}
