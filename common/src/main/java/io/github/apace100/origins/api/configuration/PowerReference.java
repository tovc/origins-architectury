package io.github.apace100.origins.api.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.registry.OriginsDynamicRegistries;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record PowerReference(Identifier power) implements IOriginsFeatureConfiguration {
	public static Codec<PowerReference> codec(String fieldName) {
		return mapCodec(fieldName).codec();
	}

	public static MapCodec<PowerReference> mapCodec(String fieldName) {
		return OriginsCodecs.IDENTIFIER.fieldOf(fieldName).xmap(PowerReference::new, PowerReference::power);
	}

	@Override
	public @NotNull List<String> getErrors(@NotNull MinecraftServer server) {
		return this.checkPower(OriginsDynamicRegistries.get(server), power).stream()
				.map(x -> "PowerReference/Missing Power: " + x.toString()).toList();
	}
}
