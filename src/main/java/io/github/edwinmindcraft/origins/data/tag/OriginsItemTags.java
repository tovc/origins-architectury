package io.github.edwinmindcraft.origins.data.tag;

import io.github.apace100.origins.Origins;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class OriginsItemTags {
	private static IOptionalNamedTag<Item> tag(String path) {
		return ItemTags.createOptional(Origins.identifier(path));
	}

	public static IOptionalNamedTag<Item> MEAT = tag("meat");
	public static IOptionalNamedTag<Item> RANGED_WEAPONS = tag("ranged_weapons");
}
