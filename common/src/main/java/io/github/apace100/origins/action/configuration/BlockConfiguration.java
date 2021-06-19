package io.github.apace100.origins.action.configuration;

import com.mojang.serialization.Codec;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record BlockConfiguration(Block block) implements IOriginsFeatureConfiguration {

	public static Codec<BlockConfiguration> codec(String name) {
		return OriginsCodecs.OPTIONAL_BLOCK.xmap(x -> new BlockConfiguration(x.orElse(null)), BlockConfiguration::getBlock).fieldOf(name).codec();
	}

	public BlockConfiguration(@Nullable Block block) {
		this.block = block;
	}

	public Optional<Block> getBlock() {
		return Optional.ofNullable(this.block);
	}
}
