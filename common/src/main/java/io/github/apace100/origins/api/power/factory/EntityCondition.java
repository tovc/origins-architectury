package io.github.apace100.origins.api.power.factory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.IConditionFactory;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import me.shedaniel.architectury.core.RegistryEntry;
import net.minecraft.entity.LivingEntity;

public abstract class EntityCondition<T extends IOriginsFeatureConfiguration> extends RegistryEntry<EntityCondition<?>> implements IConditionFactory<T, ConfiguredEntityCondition<T>, EntityCondition<T>> {
	public static final Codec<EntityCondition<?>> CODEC = OriginsRegistries.codec(OriginsRegistries.ENTITY_CONDITION);
	private final Codec<Pair<T, ConditionData>> codec;

	protected EntityCondition(Codec<T> codec) {
		this.codec = IConditionFactory.conditionCodec(codec);
	}

	@Override
	public Codec<Pair<T, ConditionData>> getConditionCodec() {
		return this.codec;
	}

	@Override
	public final ConfiguredEntityCondition<T> configure(T input, ConditionData data) {
		return new ConfiguredEntityCondition<>(this, input, data);
	}

	public boolean check(T configuration, LivingEntity entity) {
		return false;
	}

	public boolean check(T configuration, ConditionData data, LivingEntity entity) {
		return data.inverted() ^ this.check(configuration, entity);
	}
}
