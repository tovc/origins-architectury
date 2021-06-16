package io.github.apace100.origins.api.power;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public record PowerData(List<ConfiguredEntityCondition<?>> conditions, boolean hidden, int loadingPriority,
						String nameTranslationKey, String descriptionTranslationKey) {
	public static final PowerData DEFAULT = new PowerData(ImmutableList.of(), false, 0, "", "");

	public static final MapCodec<PowerData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			OriginsCodecs.listOf(ConfiguredEntityCondition.CODEC).optionalFieldOf("conditions", ImmutableList.of()).forGetter(PowerData::conditions),
			Codec.BOOL.optionalFieldOf("hidden", false).forGetter(PowerData::hidden),
			Codec.INT.optionalFieldOf("loading_priority", 0).forGetter(PowerData::loadingPriority),
			Codec.STRING.optionalFieldOf("name", "").forGetter(PowerData::nameTranslationKey),
			Codec.STRING.optionalFieldOf("description", "").forGetter(PowerData::descriptionTranslationKey)
	).apply(instance, PowerData::new));

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Completes the current definition of the power by adding the name if it couldn't be found.<br/>
	 * This is solely for use during power loading, everything else should be fine.
	 *
	 * @param identifier The identifier of this power.
	 *
	 * @return A new, completed power data.
	 */
	public PowerData complete(Identifier identifier) {
		return new PowerData(conditions, hidden, loadingPriority,
				StringUtils.isEmpty(this.nameTranslationKey) ? "power." + identifier.getNamespace() + "." + identifier.getPath() + ".name" : this.nameTranslationKey,
				StringUtils.isEmpty(this.descriptionTranslationKey) ? "power." + identifier.getNamespace() + "." + identifier.getPath() + ".description" : this.descriptionTranslationKey);
	}

	public Builder copyOf() {
		Builder builder = builder().hidden(this.hidden).withPriority(this.loadingPriority)
				.withName(this.nameTranslationKey).withDescription(this.descriptionTranslationKey);
		this.conditions.forEach(builder::addCondition);
		return builder;
	}

	public static class Builder {
		private final List<ConfiguredEntityCondition<?>> conditions = new ArrayList<>();
		private boolean hidden = false;
		private int loadingPriority = 0;
		private String name = "";
		private String description = "";

		public Builder() {

		}

		public List<ConfiguredEntityCondition<?>> getConditions() {
			return conditions;
		}

		public Builder hidden() {
			this.hidden = true;
			return this;
		}

		public Builder hidden(boolean hidden) {
			this.hidden = hidden;
			return this;
		}

		public Builder withPriority(int priority) {
			this.loadingPriority = priority;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder withIdentifier(Identifier identifier) {
			this.name = "power." + identifier.getNamespace() + "." + identifier.getPath() + ".name";
			this.description = "power." + identifier.getNamespace() + "." + identifier.getPath() + ".description";
			return this;
		}

		public Builder addCondition(ConfiguredEntityCondition<?> condition) {
			this.conditions.add(condition);
			return this;
		}

		public PowerData build() {
			return new PowerData(this.conditions, hidden, loadingPriority, name, description);
		}
	}
}
