package io.github.apace100.origins.action.meta;

import io.github.apace100.origins.api.IOriginsFeatureConfiguration;

public interface IDelegatedActionConfiguration<V> extends IOriginsFeatureConfiguration {
	void execute(V parameters);
}
