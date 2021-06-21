package io.github.apace100.origins.power;

import com.google.common.collect.ImmutableSet;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.api.power.factory.PowerFactory;
import io.github.apace100.origins.power.configuration.MultipleConfiguration;
import net.minecraft.util.Pair;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MultiplePower extends PowerFactory<MultipleConfiguration<ConfiguredPower<?, ?>>> {
	private static final ImmutableSet<String> EXCLUDED = ImmutableSet.of("type", "loading_priority", "name", "description", "hidden", "condition");
	private static final Predicate<String> ALLOWED = str -> !EXCLUDED.contains(str);

	public MultiplePower() {
		super(MultipleConfiguration.mapCodec(ConfiguredPower.CODEC, ALLOWED).codec(), false);
	}

	@Override
	public Map<String, ConfiguredPower<?, ?>> getContainedPowers(ConfiguredPower<MultipleConfiguration<ConfiguredPower<?, ?>>, ?> configuration) {
		return configuration.getConfiguration().children().entrySet().stream()
				.map(x -> new Pair<>("_" + x.getKey(), reconfigure(x.getValue())))
				.collect(Collectors.toUnmodifiableMap(Pair::getLeft, Pair::getRight));
	}

	/**
	 * Has the effect of hiding the power from the origin screen.
	 */
	private <C extends IOriginsFeatureConfiguration, F extends PowerFactory<C>> ConfiguredPower<C, ?> reconfigure(ConfiguredPower<C, F> source) {
		return source.getFactory().configure(source.getConfiguration(), source.getData().copyOf().hidden().build());
	}
}
