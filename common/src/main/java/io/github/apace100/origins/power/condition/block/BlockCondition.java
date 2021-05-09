package io.github.apace100.origins.power.condition.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.registry.Registry;

import java.util.function.Predicate;

public class BlockCondition implements Predicate<CachedBlockPosition> {

	public static final Codec<BlockCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Registry.BLOCK.fieldOf("block").forGetter(x -> x.block)
	).apply(instance, BlockCondition::new));

	private final Block block;

	public BlockCondition(Block block) {
		this.block = block;
	}

	@Override
	public boolean test(CachedBlockPosition cachedBlockPosition) {
		return cachedBlockPosition.getBlockState().isOf(this.block);
	}
}
