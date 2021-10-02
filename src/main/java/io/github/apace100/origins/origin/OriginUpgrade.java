package io.github.apace100.origins.origin;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public class OriginUpgrade {

	public static final Codec<OriginUpgrade> CODEC = io.github.edwinmindcraft.origins.api.origin.OriginUpgrade.MAP_CODEC.xmap(OriginUpgrade::new, OriginUpgrade::getWrapped).codec();

	private final io.github.edwinmindcraft.origins.api.origin.OriginUpgrade wrapped;

	public OriginUpgrade(ResourceLocation advancementCondition, ResourceLocation upgradeToOrigin, String announcement) {
		this(new io.github.edwinmindcraft.origins.api.origin.OriginUpgrade(advancementCondition, upgradeToOrigin, announcement));
	}

	public OriginUpgrade(io.github.edwinmindcraft.origins.api.origin.OriginUpgrade wrapped) {
		this.wrapped = wrapped;
	}

	public io.github.edwinmindcraft.origins.api.origin.OriginUpgrade getWrapped() {
		return this.wrapped;
	}

	public ResourceLocation getAdvancementCondition() {
		return this.wrapped.advancement();
	}

	public ResourceLocation getUpgradeToOrigin() {
		return this.wrapped.origin();
	}

	public String getAnnouncement() {
		return this.wrapped.announcement();
	}
}
