package io.github.apace100.origins.condition.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.state.property.Property;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class BlockStateCondition implements Predicate<CachedBlockPosition> {
	public static final Codec<BlockStateCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("property").forGetter(x -> x.property),
			OriginsCodecs.COMPARISON.optionalFieldOf("comparison").forGetter(x -> x.comparison),
			Codec.INT.optionalFieldOf("compare_to").forGetter(x -> x.compareTo),
			Codec.BOOL.optionalFieldOf("value").forGetter(x -> x.value),
			Codec.STRING.optionalFieldOf("enum").forGetter(x -> x.e)
	).apply(instance, BlockStateCondition::new));

	private final String property;
	private final Optional<Comparison> comparison;
	private final Optional<Integer> compareTo;
	private final Optional<Boolean> value;
	private final Optional<String> e;

	public BlockStateCondition(String property, Optional<Comparison> comparison, Optional<Integer> compareTo, Optional<Boolean> value, Optional<String> e) {
		this.property = property;
		this.comparison = comparison;
		this.compareTo = compareTo;
		this.value = value;
		this.e = e;
	}

	@Override
	public boolean test(CachedBlockPosition block) {
		BlockState state = block.getBlockState();
		Collection<Property<?>> properties = state.getProperties();
		Property<?> property = null;
		for (Property<?> p : properties) {
			if (p.getName().equals(this.property)) {
				property = p;
				break;
			}
		}
		if (property != null) {
			Object value = state.get(property);
			if (e.isPresent() && value instanceof Enum) {
				return ((Enum<?>) value).name().equalsIgnoreCase(e.get());
			} else if (this.value.isPresent() && value instanceof Boolean) {
				return value == this.value.get();
			} else if (comparison.isPresent() && compareTo.isPresent() && value instanceof Integer) {
				return comparison.get().compare((Integer) value, compareTo.get());
			}
		}
		return false;
	}
}

