package io.github.apace100.origins.api.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ConfiguredFactory<T extends IOriginsFeatureConfiguration, F> implements IOriginsFeatureConfiguration {
	private final F factory;
	private final T configuration;

	protected ConfiguredFactory(F factory, T configuration) {
		this.factory = factory;
		this.configuration = configuration;
	}

	public F getFactory() {
		return this.factory;
	}

	public T getConfiguration() {
		return this.configuration;
	}

	@Override
	public @NotNull List<String> getErrors(@NotNull MinecraftServer server) {
		return this.getConfiguration().getErrors(server);
	}

	@Override
	public @NotNull List<String> getWarnings(@NotNull MinecraftServer server) {
		return this.getConfiguration().getWarnings(server);
	}

	@Override
	public boolean isConfigurationValid() {
		return this.getConfiguration().isConfigurationValid();
	}
}
