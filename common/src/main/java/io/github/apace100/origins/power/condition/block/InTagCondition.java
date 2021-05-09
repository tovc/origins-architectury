package io.github.apace100.origins.power.condition.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.tag.Tag;

import java.util.function.Predicate;

public class InTagCondition implements Predicate<CachedBlockPosition> {

	public static final Codec<InTagCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.BLOCK_TAG.fieldOf("tag").forGetter(x -> x.tag)
	).apply(instance, InTagCondition::new));

	private final Tag<Block> tag;

	public InTagCondition(Tag<Block> tag) {
		this.tag = tag;
	}

	@Override
	public boolean test(CachedBlockPosition cachedBlockPosition) {
		return cachedBlockPosition.getBlockState().isIn(tag);
	}
}
