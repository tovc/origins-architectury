package io.github.apace100.origins.power.condition.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

public class EnchantmentCondition implements Predicate<LivingEntity> {

	public static final Codec<EnchantmentCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_ENCHANTMENT.fieldOf("enchantment").forGetter(x -> x.enchantment),
			Codec.STRING.optionalFieldOf("calculation", "sum").forGetter(x -> x.calculation),
			OriginsCodecs.COMPARISON.fieldOf("comparison").forGetter(x -> x.comparison),
			Codec.INT.fieldOf("compare_to").forGetter(x -> x.compareTo)
	).apply(instance, EnchantmentCondition::new));

	private final Optional<Enchantment> enchantment;
	private final String calculation;
	private final Comparison comparison;
	private final int compareTo;

	public EnchantmentCondition(Optional<Enchantment> enchantment, String calculation, Comparison comparison, int compareTo) {
		this.enchantment = enchantment;
		this.calculation = calculation;
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(LivingEntity entity) {
		if (!this.enchantment.isPresent())
			return false;
		int value = 0;
		Enchantment enchantment = this.enchantment.get();
		switch (calculation) {
			case "sum":
				for (ItemStack stack : enchantment.getEquipment(entity).values()) {
					value += EnchantmentHelper.getLevel(enchantment, stack);
				}
				break;
			case "max":
				value = EnchantmentHelper.getEquipmentLevel(enchantment, entity);
				break;
			default:
				Origins.LOGGER.error("Error in \"enchantment\" entity condition, undefined calculation type: \"" + calculation + "\".");
				break;
		}
		return comparison.compare(value, compareTo);
	}
}
