package io.github.apace100.origins.api.power.factory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.IConditionFactory;
import io.github.apace100.origins.api.power.configuration.ConfiguredBlockCondition;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import me.shedaniel.architectury.core.RegistryEntry;
import net.minecraft.block.pattern.CachedBlockPosition;

public abstract class BlockCondition<T extends IOriginsFeatureConfiguration> extends RegistryEntry<BlockCondition<?>> implements IConditionFactory<T, ConfiguredBlockCondition<T, ?>, BlockCondition<T>> {
	public static final Codec<BlockCondition<?>> CODEC = OriginsRegistries.codec(OriginsRegistries.BLOCK_CONDITION);

	private final Codec<Pair<T, ConditionData>> codec;

	protected BlockCondition(Codec<T> codec) {
		this.codec = IConditionFactory.conditionCodec(codec);
	}

	@Override
	public Codec<Pair<T, ConditionData>> getConditionCodec() {
		return this.codec;
	}

	@Override
	public final ConfiguredBlockCondition<T, ?> configure(T input, ConditionData data) {
		return new ConfiguredBlockCondition<>(this, input, data);
	}

	public boolean check(T configuration, CachedBlockPosition block) {
		return false;
	}

	public boolean check(T configuration, ConditionData data, CachedBlockPosition block) {
		return data.inverted() ^ this.check(configuration, block);
	}
}
