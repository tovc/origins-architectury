package io.github.apace100.origins.power.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;

import java.util.List;
import java.util.Optional;

public final class ListConfiguration<T> implements IOriginsFeatureConfiguration {
	public static <T> MapCodec<ListConfiguration<T>> mapCodec(Codec<T> codec, String singular, String plural) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				codec.optionalFieldOf(singular).forGetter(ListConfiguration::getSingular),
				OriginsCodecs.listOf(codec).optionalFieldOf(plural, ImmutableList.of()).forGetter(ListConfiguration::getMultiple)
		).apply(instance, (first, others) -> {
			ImmutableList.Builder<T> builder = ImmutableList.builder();
			first.ifPresent(builder::add);
			builder.addAll(others);
			return new ListConfiguration<>(builder.build());
		}));
	}
	public static <T> MapCodec<ListConfiguration<T>> optionalMapCodec(Codec<Optional<T>> codec, String singular, String plural) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				codec.optionalFieldOf(singular, Optional.empty()).forGetter(ListConfiguration::getSingular),
				OriginsCodecs.optionalListOf(codec).optionalFieldOf(plural, ImmutableList.of()).forGetter(ListConfiguration::getMultiple)
		).apply(instance, (first, others) -> {
			ImmutableList.Builder<T> builder = ImmutableList.builder();
			first.ifPresent(builder::add);
			builder.addAll(others);
			return new ListConfiguration<>(builder.build());
		}));
	}

	public static <T> Codec<ListConfiguration<T>> codec(Codec<T> codec, String singular, String plural) {
		return mapCodec(codec, singular, plural).codec();
	}

	public static <T> Codec<ListConfiguration<T>> optionalCodec(Codec<Optional<T>> codec, String singular, String plural) {
		return optionalMapCodec(codec, singular, plural).codec();
	}

	private final ImmutableList<T> content;

	public ListConfiguration(Iterable<T> content) {
		this.content = ImmutableList.copyOf(content);
	}

	@SafeVarargs
	public ListConfiguration(T... params) {
		this.content = ImmutableList.copyOf(params);
	}

	private Optional<T> getSingular() {
		return this.content.size() == 1 ? this.content.stream().findFirst() : Optional.empty();
	}

	private List<T> getMultiple() {
		return this.content.size() == 1 ? ImmutableList.of() : ImmutableList.copyOf(this.content);
	}

	public List<T> getContent() {
		return content;
	}
}
