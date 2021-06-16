package io.github.apace100.origins.api.power;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

public abstract class ConfiguredCondition<T extends IOriginsFeatureConfiguration, F> extends ConfiguredFactory<T, F> {
	private final ConditionData data;

	protected ConfiguredCondition(F factory, T configuration, ConditionData data) {
		super(factory, configuration);
		this.data = data;
	}

	public ConditionData getData() {
		return data;
	}
}
