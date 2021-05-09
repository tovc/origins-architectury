package io.github.apace100.origins.power.factory;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.github.apace100.origins.util.OriginsCodecs;
import me.shedaniel.architectury.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class GenericFactory<I extends GenericInstance<I, F>, F extends GenericFactory<I, F>> {

	public static <I extends GenericInstance<I, F>, F extends GenericFactory<I, F>> Codec<F> factoryCodec(Registry<F> registry) {
		return Identifier.CODEC.xmap(registry::get, registry::getId);
	}

	public static <I extends GenericInstance<I, F>, F extends GenericFactory<I, F>> Codec<I> instanceCodec(Codec<F> factoryCodec) {
		return OriginsCodecs.inlineDispatch(factoryCodec, GenericInstance::getFactory, GenericFactory::getInstanceCodec);
	}

	public static <I extends GenericInstance<I, F>, F extends GenericFactory<I, F>, E> Codec<I> instanceCodecWithDefault(Codec<F> factoryCodec, Codec<? extends I> defaultCodec) {
		Codec<I> instance = instanceCodec(factoryCodec);
		return Codec.either(instance, defaultCodec).xmap(x -> x.map(Function.identity(), Function.identity()), Either::left);
	}

	public final Codec<I> codec;

	protected GenericFactory(Codec<I> codec) {
		this.codec = codec;
	}

	protected <T> GenericFactory(Codec<? extends T> codec, BiFunction<F, T, I> to, Function<I, ? super T> from) {
		this.codec = ((Codec<T>) codec).xmap(x -> to.apply((F) this, x), (Function<I, T>) from);
	}

	public final Codec<I> getInstanceCodec() {
		return this.codec;
	}
}
