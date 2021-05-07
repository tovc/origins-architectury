package io.github.apace100.origins.integration;

public interface OriginLoadingEvent<T> {
	void onLoad(T load);
}
