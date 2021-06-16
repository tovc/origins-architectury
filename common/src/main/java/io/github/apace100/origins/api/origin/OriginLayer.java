package io.github.apace100.origins.api.origin;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record OriginLayer(int order, List<ConditionedOrigin> conditionedOrigins, boolean enabled, String name, String missingName,
						  String missingDescription, boolean allowRandom, boolean randomAllowsUnchoosable,
						  List<Identifier> randomExcluded, @Nullable Identifier defaultOrigin, boolean autoChoose,
						  boolean replace, boolean replaceRandomExlusions) implements Comparable<OriginLayer>{

	public static final int NO_ORDER = Integer.MAX_VALUE;

	public static final Codec<OriginLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("order", NO_ORDER).forGetter(OriginLayer::order),
			ConditionedOrigin.CODEC.listOf().fieldOf("origins").forGetter(OriginLayer::conditionedOrigins),
			Codec.BOOL.optionalFieldOf("enabled", true).forGetter(OriginLayer::enabled),
			Codec.STRING.optionalFieldOf("name", "").forGetter(OriginLayer::name),
			Codec.STRING.optionalFieldOf("missing_name", "").forGetter(OriginLayer::missingName),
			Codec.STRING.optionalFieldOf("missing_description", "").forGetter(OriginLayer::missingDescription),
			Codec.BOOL.optionalFieldOf("allow_random", false).forGetter(OriginLayer::allowRandom),
			Codec.BOOL.optionalFieldOf("allow_random_unchoosable", false).forGetter(OriginLayer::randomAllowsUnchoosable),
			Identifier.CODEC.listOf().optionalFieldOf("exclude_random", ImmutableList.of()).forGetter(OriginLayer::randomExcluded),
			Identifier.CODEC.optionalFieldOf("default_origin").forGetter(OriginLayer::getDefaultOrigin),
			Codec.BOOL.optionalFieldOf("auto_choose", false).forGetter(OriginLayer::autoChoose),
			Codec.BOOL.optionalFieldOf("replace", false).forGetter(OriginLayer::replace),
			Codec.BOOL.optionalFieldOf("replace_exclude_random", false).forGetter(OriginLayer::replace)
	).apply(instance, OriginLayer::new));

	public static OriginLayer DEFAULT = new OriginLayer(NO_ORDER, ImmutableList.of(), true, "", "", "", false, false, ImmutableList.of(), (Identifier) null, false, false, false);

	public OriginLayer(int order, List<ConditionedOrigin> origins, boolean enabled, String name, String missingName,
					   String missingDescription, boolean allowRandom, boolean randomAllowsUnchoosable,
					   List<Identifier> randomExcluded, Optional<Identifier> defaultOrigin, boolean autoChoose, boolean replace, boolean replaceRandomExlusions) {
		this(order, origins, enabled, name, missingName, missingDescription, allowRandom, randomAllowsUnchoosable, randomExcluded, defaultOrigin.orElse(null), autoChoose, replace, replaceRandomExlusions);
	}

	public Optional<Identifier> getDefaultOrigin() {
		return Optional.ofNullable(this.defaultOrigin());
	}

	public static Builder builder() { return new Builder(); }

	public Builder copyOf() {
		Builder builder = builder().order(this.order).enabled(this.enabled).name(this.name).missingName(this.missingName)
				.missingDescription(this.missingDescription).allowRandom(this.allowRandom).randomAllowsUnchoosable(this.randomAllowsUnchoosable)
				.defaultOrigin(this.defaultOrigin).autoChoose(this.autoChoose).replace(this.replace).replaceRandomExlusions(this.replaceRandomExlusions);
		this.conditionedOrigins.forEach(builder::addOrigin);
		this.randomExcluded.forEach(builder::addRandomExclusion);
		return builder;
	}

	public OriginLayer complete(Identifier identifier) {
		return this.copyOf().withIdentifierSafe(identifier).build();
	}

	@Override
	public int compareTo(@NotNull OriginLayer o) {
		return Integer.compare(this.order(), o.order());
	}

	public boolean hasDefaultOrigin() {
		return this.defaultOrigin() != null;
	}

	public Stream<Identifier> origins(PlayerEntity player) {
		return this.conditionedOrigins().stream().filter(x -> x.check(player)).flatMap(x -> x.origins().stream());
	}


	public Stream<Identifier> origins() {
		return this.conditionedOrigins().stream().flatMap(x -> x.origins().stream());
	}

	public Stream<Identifier> randomOrigins(PlayerEntity player) {
		return origins(player).filter(x -> !this.randomExcluded().contains(x) && (this.randomAllowsUnchoosable() || Origin.isChoosable(x)));
	}

	public int optionCount(PlayerEntity player) {
		return Math.toIntExact(this.origins(player).filter(Origin::isChoosable).count() + (this.allowRandom() && this.randomOrigins(player).findAny().isPresent() ? 1 : 0));
	}

	public static class Builder {
		private final List<ConditionedOrigin> origins = new ArrayList<>();
		private final List<Identifier> randomExcluded = new ArrayList<>();
		private int order = Integer.MAX_VALUE;
		private boolean enabled = true;
		private String name = "";
		private String missingName = "";
		private String missingDescription = "";
		private boolean allowRandom = false;
		private boolean randomAllowsUnchoosable = false;
		private Identifier defaultOrigin = null;
		private boolean autoChoose = false;
		private boolean replace = false;
		private boolean replaceRandomExlusions = false;

		public Builder() { }

		public List<ConditionedOrigin> getOrigins() {
			return origins;
		}

		public List<Identifier> getRandomExcluded() {
			return randomExcluded;
		}

		public Builder order(int order) {
			this.order = order;
			return this;
		}

		public Builder addOrigin(ConditionedOrigin origin) {
			this.origins.add(origin);
			return this;
		}

		public Builder addOrigin(Identifier... identifiers) {
			for (Identifier id : identifiers)
				this.addOrigin(new ConditionedOrigin(id));
			return this;
		}

		public Builder addRandomExclusion(Identifier... excluded) {
			this.randomExcluded.addAll(Arrays.asList(excluded));
			return this;
		}

		public Builder withIdentifier(Identifier identifier) {
			this.name = "layer." + identifier.getNamespace() + "." + identifier.getPath() + ".name";
			this.missingName = "layer." + identifier.getNamespace() + "." + identifier.getPath() + ".missing_origin.name";
			this.missingDescription = "layer." + identifier.getNamespace() + "." + identifier.getPath() + ".missing_origin.description";
			return this;
		}

		public Builder withIdentifierSafe(Identifier identifier) {
			if (StringUtils.isEmpty(this.name)) this.name = "layer." + identifier.getNamespace() + "." + identifier.getPath() + ".name";
			if (StringUtils.isEmpty(this.name)) this.missingName = "layer." + identifier.getNamespace() + "." + identifier.getPath() + ".missing_origin.name";
			if (StringUtils.isEmpty(this.name)) this.missingDescription = "layer." + identifier.getNamespace() + "." + identifier.getPath() + ".missing_origin.description";
			return this;
		}

		public Builder enabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public Builder allowRandom(boolean allowRandom) {
			this.allowRandom = allowRandom;
			return this;
		}

		public Builder randomAllowsUnchoosable(boolean randomAllowsUnchoosable) {
			this.randomAllowsUnchoosable = randomAllowsUnchoosable;
			return this;
		}

		public Builder autoChoose(boolean autoChoose) {
			this.autoChoose = autoChoose;
			return this;
		}

		public Builder replace(boolean replace) {
			this.replace = replace;
			return this;
		}

		public Builder replaceRandomExlusions(boolean replaceRandomExlusions) {
			this.replaceRandomExlusions = replaceRandomExlusions;
			return this;
		}

		public Builder replace() {
			return this.replace(true);
		}

		public Builder replaceRandomExlusions() {
			return this.replaceRandomExlusions(true);
		}

		public Builder autoChoose() {
			return this.autoChoose(true);
		}


		public Builder defaultOrigin(@Nullable Identifier defaultOrigin) {
			this.defaultOrigin = defaultOrigin;
			return this;
		}

		public Builder allowRandom() {
			return this.allowRandom(true);
		}

		public Builder randomAllowsUnchoosable() {
			return this.randomAllowsUnchoosable(true);
		}

		public Builder disabled() {
			return this.enabled(false);
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder missingName(String missingName) {
			this.missingName = missingName;
			return this;
		}

		public Builder missingDescription(String missingDescription) {
			this.missingDescription = missingDescription;
			return this;
		}

		public OriginLayer build() {
			return new OriginLayer(order, ImmutableList.copyOf(origins), enabled, name, missingName, missingDescription,
					allowRandom, randomAllowsUnchoosable, ImmutableList.copyOf(randomExcluded), defaultOrigin,
					autoChoose, replace, replaceRandomExlusions);
		}

		//FIXME this isn't done correctly as the actual version should only replace overwritten properties.
		public Builder merge(OriginLayer other) {
			if (other.order() != NO_ORDER) this.order(other.order());
			if (!other.enabled()) this.disabled();
			other.conditionedOrigins().forEach(this::addOrigin);
			if (!StringUtils.isEmpty(other.name())) this.name(other.name());
			if (!StringUtils.isEmpty(other.missingName())) this.name(other.missingName());
			if (!StringUtils.isEmpty(other.missingDescription())) this.name(other.missingDescription());
			if (other.allowRandom()) this.allowRandom();
			if (other.randomAllowsUnchoosable()) this.randomAllowsUnchoosable();
			//TODO Replace random exclusion
			if (other.replaceRandomExlusions()) this.randomExcluded.clear();
			other.randomExcluded().forEach(this::addRandomExclusion);
			if (other.defaultOrigin() != null) this.defaultOrigin(other.defaultOrigin());
			if (other.autoChoose()) this.autoChoose();
			return this.replace(false).replaceRandomExlusions(false);
		}

		public Builder merge(Builder other) {
			return this.merge(other.build());
		}
	}
}
