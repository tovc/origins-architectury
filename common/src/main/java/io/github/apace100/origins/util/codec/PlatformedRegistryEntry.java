package io.github.apace100.origins.util.codec;

import com.mojang.serialization.DataResult;
import jdk.internal.joptsimple.internal.Strings;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Function;

public class PlatformedRegistryEntry {
	public final Identifier value;
	public final String platform;
	public final boolean optional;

	public PlatformedRegistryEntry(Identifier value, String platform, boolean optional) {
		this.value = value;
		this.platform = platform;
		this.optional = optional;
	}

	public <T> DataResult<Optional<T>> get(Function<Identifier, Optional<T>> accessor) {
		Optional<T> apply = accessor.apply(this.value);
		if (optional || (!Strings.isNullOrEmpty(this.platform) && !Platform.getModLoader().equalsIgnoreCase(this.platform)))
			return DataResult.success(apply);
		if (!apply.isPresent())
			return DataResult.error("Object \"" + this.value + "\" couldn't be found.", apply);
		return DataResult.success(apply);
	}
}
