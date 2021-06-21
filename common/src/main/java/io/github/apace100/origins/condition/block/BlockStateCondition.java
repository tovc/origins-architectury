package io.github.apace100.origins.condition.block;

import io.github.apace100.origins.api.power.factory.BlockCondition;
import io.github.apace100.origins.condition.configuration.BlockStateConfiguration;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.state.property.Property;

import java.util.Collection;

public class BlockStateCondition extends BlockCondition<BlockStateConfiguration> {

	public BlockStateCondition() {
		super(BlockStateConfiguration.CODEC);
	}

	@Override
	protected boolean check(BlockStateConfiguration configuration, CachedBlockPosition block) {
		BlockState state = block.getBlockState();
		Collection<Property<?>> properties = state.getProperties();
		return properties.stream().filter(p -> configuration.property().equals(p.getName())).findFirst().map(property -> configuration.checkProperty(state.get(property))).orElse(false);
	}
}

