package io.github.apace100.origins.api.power;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

/**
 * A generic type to define an action.
 *
 * @param <T> The type of the {@link IOriginsFeatureConfiguration} this factory will accept.
 * @param <C> The type of the {@link ConfiguredFactory} this factory will instantiate.
 * @param <F> The type of this {@link IFactory}.
 */
public interface IFactory<T extends IOriginsFeatureConfiguration, C extends ConfiguredFactory<T, ? extends F>, F extends IFactory<T, C, F>> extends Codec<C> {

	/**
	 * Gets or create a {@link MapCodec} from the given {@link Codec}
	 * @param codec The codec to transform
	 * @return Either the codec itself if it was a boxed MapCodec, or a field with name "value"
	 */
	static <T> MapCodec<T> asMap(Codec<T> codec) {
		if (codec instanceof MapCodec.MapCodecCodec)
			return ((MapCodec.MapCodecCodec<T>) codec).codec();
		return codec.fieldOf("value");
	}

	/**
	 * Accesses the {@link Codec} used to serialize the configuration.
	 * @return The codec used to serialize the configuration.
	 */
	Codec<T> getCodec();

	/**
	 * Configures a new {@link ConfiguredFactory} from the given parameters.
	 * @param input
	 * @return
	 */
	C configure(T input);

	@Override
	default <T1> DataResult<Pair<C, T1>> decode(DynamicOps<T1> ops, T1 input) {
		return this.getCodec().decode(ops, input).map(x -> x.mapFirst(this::configure));
	}

	@Override
	default <T1> DataResult<T1> encode(C input, DynamicOps<T1> ops, T1 prefix) {
		return this.getCodec().encode(input.getConfiguration(), ops, prefix);
	}
}
