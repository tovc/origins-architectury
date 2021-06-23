package io.github.apace100.origins.action.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.api.registry.OriginsDynamicRegistries;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ChangeResourceConfiguration(Identifier resource, int amount) implements IOriginsFeatureConfiguration {

	public static final Codec<ChangeResourceConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.IDENTIFIER.fieldOf("resource").forGetter(ChangeResourceConfiguration::resource),
			Codec.INT.fieldOf("change").forGetter(ChangeResourceConfiguration::amount)
	).apply(instance, ChangeResourceConfiguration::new));

	@Override
	public @NotNull List<String> getErrors(@NotNull MinecraftServer server) {
		return this.checkPower(OriginsDynamicRegistries.get(server), this.resource()).stream().map(x -> "Missing power: %s".formatted(x.toString())).toList();
	}

	@Override
	public @NotNull List<String> getWarnings(@NotNull MinecraftServer server) {
		if (this.amount() == 0)
			return ImmutableList.of("Change expected, was 0");
		return ImmutableList.of();
	}

	@Override
	public boolean isConfigurationValid() {
		return this.amount() != 0;
	}
}
