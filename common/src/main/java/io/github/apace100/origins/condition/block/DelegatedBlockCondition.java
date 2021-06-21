package io.github.apace100.origins.condition.block;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.power.factory.BlockCondition;
import io.github.apace100.origins.condition.meta.IDelegatedConditionConfiguration;
import net.minecraft.block.pattern.CachedBlockPosition;

public class DelegatedBlockCondition<T extends IDelegatedConditionConfiguration<CachedBlockPosition>> extends BlockCondition<T> {
	public DelegatedBlockCondition(Codec<T> codec) {
		super(codec);
	}

	@Override
	protected boolean check(T configuration, CachedBlockPosition position) {
		return configuration.check(position);
	}
}
