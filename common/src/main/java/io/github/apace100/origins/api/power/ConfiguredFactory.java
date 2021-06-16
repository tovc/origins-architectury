package io.github.apace100.origins.api.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

public abstract class ConfiguredFactory<T extends IOriginsFeatureConfiguration, F> {
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
}
