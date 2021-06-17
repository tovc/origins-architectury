package io.github.apace100.origins.api.power.factory;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.ConditionData;
import io.github.apace100.origins.api.power.IConditionFactory;
import io.github.apace100.origins.api.power.configuration.ConfiguredDamageCondition;
import io.github.apace100.origins.api.registry.OriginsRegistries;
import me.shedaniel.architectury.core.RegistryEntry;
import net.minecraft.entity.damage.DamageSource;

public abstract class DamageCondition<T extends IOriginsFeatureConfiguration> extends RegistryEntry<DamageCondition<?>> implements IConditionFactory<T, ConfiguredDamageCondition<T, ?>, DamageCondition<T>> {
	public static final Codec<DamageCondition<?>> CODEC = OriginsRegistries.codec(OriginsRegistries.DAMAGE_CONDITION);

	private final Codec<Pair<T, ConditionData>> codec;

	protected DamageCondition(Codec<T> codec) {
		this.codec = IConditionFactory.conditionCodec(codec);
	}

	@Override
	public Codec<Pair<T, ConditionData>> getConditionCodec() {
		return this.codec;
	}

	@Override
	public final ConfiguredDamageCondition<T, ?> configure(T input, ConditionData data) {
		return new ConfiguredDamageCondition<>(this, input, data);
	}

	protected boolean check(T configuration, DamageSource source, float amount) {
		return false;
	}

	public boolean check(T configuration, ConditionData data, DamageSource source, float amount) {
		return data.inverted() ^ this.check(configuration, data, source, amount);
	}
}
