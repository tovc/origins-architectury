package io.github.apace100.origins.util.codec;

import com.mojang.serialization.DataResult;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Function;

public class OptionalField {
	public final Identifier value;
	public final boolean optional;

	public OptionalField(Identifier value, boolean optional) {
		this.value = value;
		this.optional = optional;
	}

	public <T> DataResult<Optional<T>> get(Function<Identifier, Optional<T>> accessor) {
		Optional<T> apply = accessor.apply(this.value);
		if (optional)
			return DataResult.success(apply);
		if (!apply.isPresent())
			return DataResult.error("Object \"" + this.value + "\" couldn't be found.", apply);
		return DataResult.success(apply);
	}
}
