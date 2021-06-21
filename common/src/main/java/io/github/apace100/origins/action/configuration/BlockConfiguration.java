package io.github.apace100.origins.action.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record BlockConfiguration(Block block) implements IOriginsFeatureConfiguration {

	public static Codec<BlockConfiguration> codec(String name) {
		return OriginsCodecs.OPTIONAL_BLOCK.fieldOf(name).xmap(x -> new BlockConfiguration(x.orElse(null)), BlockConfiguration::getBlock).codec();
	}

	public BlockConfiguration(@Nullable Block block) {
		this.block = block;
	}

	public Optional<Block> getBlock() {
		return Optional.ofNullable(this.block);
	}

	@Override
	public @NotNull List<String> getWarnings(@NotNull MinecraftServer server) {
		if (this.block() == null)
			return ImmutableList.of("Block/Missing block");
		return IOriginsFeatureConfiguration.super.getWarnings(server);
	}

	@Override
	public boolean isConfigurationValid() {
		return this.block() != null;
	}
}
