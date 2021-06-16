package io.github.apace100.origins.api.power;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

/**
 * Condition factories provide two methods for check.<br/>
 * Most of the time you won't need to override the version with {@link ConditionData},
 * but it may be useful to optimize the code.
 */
public interface IConditionFactory<T extends IOriginsFeatureConfiguration, C extends ConfiguredCondition<T, F>, F extends IConditionFactory<T, C, F>> extends IFactory<T, C, F> {
	static <T extends IOriginsFeatureConfiguration, F> Codec<Pair<T, ConditionData>> conditionCodec(Codec<T> codec) {
		return RecordCodecBuilder.create(instance -> instance.group(
				IFactory.asMap(codec).forGetter(Pair::getFirst),
				ConditionData.CODEC.forGetter(Pair::getSecond)
		).apply(instance, Pair::new));
	}

	Codec<Pair<T, ConditionData>> getConditionCodec();

	@Override
	default Codec<T> getCodec() {
		return getConditionCodec().xmap(Pair::getFirst, t -> new Pair<>(t, ConditionData.DEFAULT));
	}

	default C configure(T input) {
		return this.configure(input, ConditionData.DEFAULT);
	}

	@Override
	default <T1> DataResult<Pair<C, T1>> decode(DynamicOps<T1> ops, T1 input) {
		return this.getConditionCodec().decode(ops, input).map(pair -> pair.mapFirst(data -> this.configure(data.getFirst(), data.getSecond())));
	}

	@Override
	default <T1> DataResult<T1> encode(C input, DynamicOps<T1> ops, T1 prefix) {
		return this.getConditionCodec().encode(Pair.of(input.getConfiguration(), input.getData()), ops, prefix);
	}

	C configure(T input, ConditionData configuration);
}
