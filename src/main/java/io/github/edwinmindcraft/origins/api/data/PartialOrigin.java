package io.github.edwinmindcraft.origins.api.data;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import io.github.apace100.origins.data.CompatibilityDataTypes;
import io.github.apace100.origins.origin.Impact;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginUpgrade;
import io.github.edwinmindcraft.origins.api.util.JsonUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Set;

public record PartialOrigin(@NotNull Set<ResourceLocation> powers,
							@Nullable ItemStack icon, @Nullable Boolean unchoosable,
							@Nullable Integer order,
							@Nullable Impact impact, @Nullable String name,
							@Nullable String description,
							@NotNull Set<OriginUpgrade> upgrades,
							int loadingOrder) {

	public Origin create(ResourceLocation name) {
		return new Origin(
				ImmutableSet.copyOf(this.powers()),
				this.icon() != null ? this.icon() : ItemStack.EMPTY,
				this.unchoosable() != null ? this.unchoosable() : false,
				this.order() != null ? this.order() : Integer.MAX_VALUE,
				this.impact() != null ? this.impact() : Impact.NONE,
				new TranslatableComponent(this.name() != null ? this.name() : "origin." + name.getNamespace() + "." + name.getPath() + ".name"),
				new TranslatableComponent(this.description() != null ? this.description() : "origin." + name.getNamespace() + "." + name.getPath() + ".description"),
				ImmutableSet.copyOf(this.upgrades()));
	}

	public enum Serializer implements JsonSerializer<PartialOrigin>, JsonDeserializer<PartialOrigin> {
		INSTANCE;

		@Override
		public PartialOrigin deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject root = GsonHelper.convertToJsonObject(json, "root element");
			Builder builder = builder();
			builder.powers(JsonUtils.getIdentifierList(root, "powers"));
			if (root.has("icon")) {
				JsonElement icon = root.get("icon");
				ItemStack read = CompatibilityDataTypes.ITEM_OR_ITEM_STACK.read(icon);
				if (read != null) builder.icon(read);
			}
			JsonUtils.getOptional(root, "unchoosable", GsonHelper::getAsBoolean).ifPresent(builder::unchoosable);
			JsonUtils.getOptional(root, "order", GsonHelper::getAsInt).ifPresent(builder::order);
			JsonUtils.getOptional(root, "impact", GsonHelper::getAsInt).ifPresent(x -> {
				if (x < 0 || x >= Impact.values().length)
					throw new JsonParseException("Impact must be between 0 and " + (Impact.values().length - 1) + ", was " + x);
				builder.impact(Impact.values()[x]);
			});
			JsonUtils.getOptional(root, "name", GsonHelper::getAsString).ifPresent(builder::name);
			JsonUtils.getOptional(root, "description", GsonHelper::getAsString).ifPresent(builder::description);
			builder.upgrades(JsonUtils.getOptionalList(root, "upgrades", (x, s) -> context.deserialize(x, OriginUpgrade.class)));
			JsonUtils.getOptional(root, "loading_priority", GsonHelper::getAsInt).ifPresent(builder::loadingOrder);
			return builder.build();
		}

		@Override
		public JsonElement serialize(PartialOrigin src, Type typeOfSrc, JsonSerializationContext context) {
			return null;
		}
	}

	public static Builder builder() {return new Builder();}

	public static final class Builder {

		private final ImmutableSet.Builder<ResourceLocation> powers = ImmutableSet.builder();
		private final ImmutableSet.Builder<OriginUpgrade> upgrades = ImmutableSet.builder();

		private ItemStack icon;
		private Boolean unchoosable;
		private Integer order;
		private Impact impact;
		private String name;
		private String description;
		private int loadingOrder = 0;

		private Builder() {}

		public Builder powers(Iterable<ResourceLocation> powers) {
			this.powers.addAll(powers);
			return this;
		}

		public Builder powers(ResourceLocation... powers) {
			this.powers.add(powers);
			return this;
		}

		public Builder icon(ItemStack icon) {
			this.icon = icon;
			return this;
		}

		public Builder unchoosable(Boolean unchoosable) {
			this.unchoosable = unchoosable;
			return this;
		}

		public Builder order(Integer order) {
			this.order = order;
			return this;
		}

		public Builder impact(Impact impact) {
			this.impact = impact;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder upgrades(Iterable<OriginUpgrade> upgrades) {
			this.upgrades.addAll(upgrades);
			return this;
		}

		public Builder upgrades(OriginUpgrade... upgrades) {
			this.upgrades.add(upgrades);
			return this;
		}

		public Builder loadingOrder(int loadingOrder) {
			this.loadingOrder = loadingOrder;
			return this;
		}

		public PartialOrigin build() {return new PartialOrigin(this.powers.build(), this.icon, this.unchoosable, this.order, this.impact, this.name, this.description, this.upgrades.build(), this.loadingOrder);}
	}
}
