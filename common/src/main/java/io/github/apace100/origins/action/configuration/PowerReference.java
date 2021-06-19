package io.github.apace100.origins.action.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.registry.OriginsDynamicRegistries;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record PowerReference(Identifier power) implements IOriginsFeatureConfiguration {
	public static Codec<PowerReference> codec(String fieldName) {
		return OriginsCodecs.POWER_TYPE.fieldOf(fieldName).xmap(PowerReference::new, PowerReference::power).codec();
	}

	@Override
	public @NotNull List<String> getErrors(@NotNull MinecraftServer server) {
		return this.checkPower(OriginsDynamicRegistries.get(server), power).stream()
				.map(x -> "PowerReference/Missing Power: " + x.toString()).toList();
	}
}
