package io.github.apace100.origins.condition.configuration;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.configuration.IntegerComparisonConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.IntStream;

public record EnchantmentConfiguration(IntegerComparisonConfiguration comparison,
									   @Nullable Enchantment enchantment,
									   Calculation calculation) implements IOriginsFeatureConfiguration {
	public static final Codec<EnchantmentConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			IntegerComparisonConfiguration.MAP_CODEC.forGetter(EnchantmentConfiguration::comparison),
			OriginsCodecs.OPTIONAL_ENCHANTMENT.fieldOf("enchantment").forGetter(x -> Optional.ofNullable(x.enchantment())),
			OriginsCodecs.enumCodec(Calculation.values(), ImmutableMap.of()).optionalFieldOf("calculation", Calculation.SUM).forGetter(EnchantmentConfiguration::calculation)
	).apply(instance, (t1, t2, t3) -> new EnchantmentConfiguration(t1, t2.orElse(null), t3)));

	@Override
	public @NotNull List<String> getErrors(@NotNull MinecraftServer server) {
		return IOriginsFeatureConfiguration.super.getErrors(server);
	}

	@Override
	public @NotNull List<String> getWarnings(@NotNull MinecraftServer server) {
		if (this.enchantment() == null)
			return ImmutableList.of(this.name() + "/Missing Enchantment");
		return ImmutableList.of();
	}

	public boolean applyCheck(Iterable<ItemStack> input) {
		if (this.enchantment() == null)
			return false;
		return this.comparison().check(this.calculation().apply(Streams.stream(input).mapToInt(stack -> EnchantmentHelper.getLevel(this.enchantment(), stack))).orElse(0));
	}

	public boolean applyCheck(ItemStack... stacks) {
		return this.applyCheck(stacks);
	}

	public enum Calculation {
		SUM(x -> x.reduce(Integer::sum)),
		MAX(IntStream::max);

		private final Function<IntStream, OptionalInt> collapse;

		Calculation(Function<IntStream, OptionalInt> collapse) {
			this.collapse = collapse;
		}

		public OptionalInt apply(IntStream stream) {
			return this.collapse.apply(stream);
		}
	}
}
