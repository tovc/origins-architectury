package io.github.apace100.origins.factory.meta;

import com.google.common.collect.ImmutableList;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Used to avoid boilerplate code by implementing {@link IOriginsFeatureConfiguration#getWarnings(MinecraftServer)} and
 * {@link IOriginsFeatureConfiguration#getErrors(MinecraftServer)} for children.
 */
public interface IStreamConfiguration<T> extends IOriginsFeatureConfiguration{
	List<T> entries();
	String name();

	@Override
	default @NotNull List<String> getErrors(@NotNull MinecraftServer server) {
		int index = 0;
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		for (T entry : this.entries()) {
			final int i = index++;
			if (entry instanceof IOriginsFeatureConfiguration config)
				builder.addAll(config.getErrors(server).stream().map(x -> "%s[%d]/%s".formatted(this.name(), i, x)).toList());
		}
		return builder.build();
	}

	@Override
	default @NotNull List<String> getWarnings(@NotNull MinecraftServer server) {
		int index = 0;
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		if (this.entries().isEmpty())
			builder.add("%s/No entries".formatted(this.name()));
		for (T entry : this.entries()) {
			final int i = index++;
			if (entry instanceof IOriginsFeatureConfiguration config)
				builder.addAll(config.getWarnings(server).stream().map(x -> "%s[%d]/%s".formatted(this.name(), i, x)).toList());
		}
		return builder.build();
	}
}
