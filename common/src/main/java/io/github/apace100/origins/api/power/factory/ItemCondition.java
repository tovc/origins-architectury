package io.github.apace100.origins.api.power.factory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.IConditionFactory;
import io.github.apace100.origins.api.power.configuration.ConfiguredItemCondition;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import me.shedaniel.architectury.core.RegistryEntry;
import net.minecraft.item.ItemStack;

public abstract class ItemCondition<T extends IOriginsFeatureConfiguration> extends RegistryEntry<ItemCondition<?>> implements IConditionFactory<T, ConfiguredItemCondition<T>, ItemCondition<T>> {
	public static final Codec<ItemCondition<?>> CODEC = OriginsRegistries.codec(OriginsRegistries.ITEM_CONDITION);
	private final Codec<Pair<T, ConditionData>> codec;

	protected ItemCondition(Codec<T> codec) {
		this.codec = IConditionFactory.conditionCodec(codec);
	}

	@Override
	public Codec<Pair<T, ConditionData>> getConditionCodec() {
		return this.codec;
	}

	@Override
	public final ConfiguredItemCondition<T> configure(T input, ConditionData data) {
		return new ConfiguredItemCondition<>(this, input, data);
	}

	public boolean check(T configuration, ItemStack stack) {
		return false;
	}

	public boolean check(T configuration, ConditionData data, ItemStack stack) {
		return data.inverted() ^ this.check(configuration, stack);
	}
}
