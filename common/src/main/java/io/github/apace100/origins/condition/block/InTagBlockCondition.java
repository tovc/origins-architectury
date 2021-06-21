package io.github.apace100.origins.condition.block;

import io.github.apace100.origins.api.configuration.FieldConfiguration;
import io.github.apace100.origins.api.power.factory.BlockCondition;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.tag.Tag;

public class InTagBlockCondition extends BlockCondition<FieldConfiguration<Tag<Block>>> {

	public InTagBlockCondition() {
		super(FieldConfiguration.codec(OriginsCodecs.BLOCK_TAG, "tag"));
	}

	@Override
	protected boolean check(FieldConfiguration<Tag<Block>> configuration, CachedBlockPosition block) {
		return block.getBlockState().isIn(configuration.value());
	}
}
