package io.github.apace100.origins.api.origin;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsDynamicRegistryManager;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.registry.OriginsDynamicRegistries;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record Origin(Set<Identifier> powers, ItemStack displayItem,
					 Impact impact, boolean choosable, int order,
					 int loadingPriority, Set<OriginUpgrade> upgrades,
					 boolean special, String name,
					 String descriptionTranslationKey) implements IOriginsFeatureConfiguration {

	public static final Codec<Origin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.setOf(Identifier.CODEC).optionalFieldOf("powers", ImmutableSet.of()).forGetter(Origin::powers),
			OriginsCodecs.ITEM_OR_ITEM_STACK.optionalFieldOf("icon", new ItemStack(Items.AIR)).forGetter(Origin::displayItem),
			OriginsCodecs.IMPACT.optionalFieldOf("unchoosable", Impact.NONE).forGetter(Origin::impact),
			Codec.BOOL.xmap(x -> !x, x -> !x).optionalFieldOf("unchoosable", false).forGetter(Origin::choosable),
			Codec.INT.optionalFieldOf("order", Integer.MAX_VALUE).forGetter(Origin::order),
			Codec.INT.optionalFieldOf("loading_priority", 0).forGetter(Origin::loadingPriority),
			OriginsCodecs.setOf(OriginUpgrade.CODEC).optionalFieldOf("upgrades", ImmutableSet.of()).forGetter(Origin::upgrades),
			Codec.BOOL.optionalFieldOf("special", false).forGetter(Origin::special),
			Codec.STRING.optionalFieldOf("name", "").forGetter(Origin::name),
			Codec.STRING.optionalFieldOf("description", "").forGetter(Origin::descriptionTranslationKey)
	).apply(instance, Origin::new));

	public static Builder builder() {
		return new Builder();
	}

	public static boolean isChoosable(Identifier identifier) {
		return OriginsAPI.getOrigins().getOrEmpty(identifier).map(Origin::choosable).orElse(false);
	}

	@NotNull
	public static Origin get(Identifier identifier) {
		return OriginsAPI.getOrigins().getOrEmpty(identifier).orElseThrow(() -> new RuntimeException("Tried to access invalid origin at runtime in a nullsafe method."));
	}

	public Builder copyOf() {
		Builder builder = builder()
				.withDisplay(displayItem)
				.withImpact(impact)
				.withOrder(order)
				.withLoadingPriority(loadingPriority)
				.name(name)
				.description(descriptionTranslationKey);
		this.powers.forEach(builder::addPower);
		this.upgrades.forEach(builder::addUpgrade);
		if (!this.choosable) builder.disableChoice();
		if (this.special) builder.special();
		return builder;
	}

	public Origin complete(Identifier identifier) {
		return this.copyOf().withIdentifierSafe(identifier).build();
	}

	@Override
	public @NotNull List<String> getErrors(@NotNull MinecraftServer server) {
		IOriginsDynamicRegistryManager dynamicRegistry = OriginsDynamicRegistries.get(server);
		return this.checkPower(dynamicRegistry, this.powers.toArray(Identifier[]::new)).stream().map(x -> "Unregistered power" + x).toList();
	}

	public static class Builder {
		public final Set<Identifier> powers;
		public final Set<OriginUpgrade> upgrades;
		public ItemStack displayItem = ItemStack.EMPTY;
		public Impact impact = Impact.NONE;
		public boolean isChoosable = true;
		public int order = 0;
		public int loadingPriority = 0;
		public boolean special = false;
		public String nameTranslationKey;
		public String descriptionTranslationKey;

		public Builder() {
			this.powers = new HashSet<>();
			this.upgrades = new HashSet<>();
		}

		public Builder withIdentifier(Identifier identifier) {
			this.nameTranslationKey = "origin." + identifier.getNamespace() + "." + identifier.getPath() + ".name";
			this.descriptionTranslationKey = "origin." + identifier.getNamespace() + "." + identifier.getPath() + ".description";
			return this;
		}

		/**
		 * Adds translation keys for name and description if those are missing.
		 */
		public Builder withIdentifierSafe(Identifier identifier) {
			if (StringUtils.isEmpty(this.nameTranslationKey)) this.nameTranslationKey = "origin." + identifier.getNamespace() + "." + identifier.getPath() + ".name";
			if (StringUtils.isEmpty(this.descriptionTranslationKey)) this.descriptionTranslationKey = "origin." + identifier.getNamespace() + "." + identifier.getPath() + ".description";
			return this;
		}

		public Set<Identifier> getPowers() {
			return powers;
		}

		public Set<OriginUpgrade> getUpgrades() {
			return upgrades;
		}

		public Builder addPower(Identifier power) {
			this.powers.add(power);
			return this;
		}

		public Builder addUpgrade(OriginUpgrade upgrade) {
			this.upgrades.add(upgrade);
			return this;
		}

		public Builder withDisplay(ItemStack stack) {
			this.displayItem = stack.copy();
			return this;
		}

		public Builder withImpact(Impact impact) {
			this.impact = impact;
			return this;
		}

		public Builder disableChoice() {
			this.isChoosable = false;
			return this;
		}

		public Builder withOrder(int order) {
			this.order = order;
			return this;
		}

		public Builder withLoadingPriority(int priority) {
			this.loadingPriority = priority;
			return this;
		}

		public Builder special() {
			this.special = true;
			return this;
		}

		public Builder name(String nameTranslationKey) {
			this.nameTranslationKey = nameTranslationKey;
			return this;
		}

		public Builder description(String descriptionTranslationKey) {
			this.descriptionTranslationKey = descriptionTranslationKey;
			return this;
		}

		public Origin build() {
			return new Origin(ImmutableSet.copyOf(this.powers), this.displayItem, this.impact,
					this.isChoosable, this.order, this.loadingPriority, ImmutableSet.copyOf(this.upgrades),
					this.special, this.nameTranslationKey, this.descriptionTranslationKey);
		}
	}
}
