package io.github.apace100.origins.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record PositionedItemStack(int position, ItemStack stack) implements Comparable<PositionedItemStack> {
	public static final Codec<Optional<PositionedItemStack>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_ITEM.fieldOf("item").forGetter(x -> x.map( k -> k.stack().getItem())),
			Codec.INT.optionalFieldOf("amount", 1).forGetter(x -> x.map(k -> k.stack().getCount()).orElse(1)),
			OriginsCodecs.NBT.optionalFieldOf("tag").forGetter(x -> x.map(k -> k.stack().getTag())),
			Codec.INT.optionalFieldOf("slot", Integer.MIN_VALUE).forGetter(x -> x.map(PositionedItemStack::position).orElse(Integer.MIN_VALUE))
	).apply(instance, (t1, t2, t3, t4) -> t1.map(item -> {
		ItemStack itemStack = new ItemStack(item, t2);
		t3.ifPresent(itemStack::setTag);
		return new PositionedItemStack(t4, itemStack);
	})));

	public boolean hasPosition() {
		return this.position() != Integer.MIN_VALUE;
	}

	@Override
	public int compareTo(@NotNull PositionedItemStack o) {
		return Integer.compare(this.position(), o.position());
	}
}
