package io.github.apace100.origins.power.condition.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;

import java.util.Optional;
import java.util.function.Predicate;

public class BlockCondition implements Predicate<CachedBlockPosition> {

	public static final Codec<BlockCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_BLOCK.fieldOf("block").forGetter(x -> x.block)
	).apply(instance, BlockCondition::new));

	private final Optional<Block> block;

	public BlockCondition(Optional<Block> block) {
		this.block = block;
	}

	@Override
	public boolean test(CachedBlockPosition cachedBlockPosition) {
		return this.block.map(x -> cachedBlockPosition.getBlockState().isOf(x)).orElse(false);
	}
}
