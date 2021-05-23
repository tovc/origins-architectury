package io.github.apace100.origins.power.condition.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import java.util.Optional;
import java.util.function.Predicate;

public class EnchantmentCondition implements Predicate<ItemStack> {

	public static final Codec<EnchantmentCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_ENCHANTMENT.fieldOf("enchantment").forGetter(x -> x.enchantment),
			OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(x -> x.comparison),
			Codec.INT.fieldOf("compare_to").forGetter(x -> x.compareTo)
	).apply(instance, EnchantmentCondition::new));

	private final Optional<Enchantment> enchantment;
	private final Comparison comparison;
	private final int compareTo;

	public EnchantmentCondition(Optional<Enchantment> enchantment, Comparison comparison, int compareTo) {
		this.enchantment = enchantment;
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(ItemStack itemStack) {
		return enchantment.map(x -> comparison.compare(EnchantmentHelper.getLevel(x, itemStack), compareTo)).orElse(false);
	}
}
