package io.github.edwinmindcraft.origins.data.tag;

import io.github.apace100.origins.Origins;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class OriginsBlockTags {
	private static IOptionalNamedTag<Block> tag(String path) {
		return BlockTags.createOptional(Origins.identifier(path));
	}

	public static IOptionalNamedTag<Block> COBWEBS = tag("cobwebs");
}
