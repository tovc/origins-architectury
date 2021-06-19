package io.github.apace100.origins.factory;

public abstract class GenericInstance<I extends GenericInstance<I, F>, F extends GenericFactory<I, F>> {
	private final F factory;

	protected GenericInstance(F factory) {
		this.factory = factory;
	}

	public F getFactory() {
		return factory;
	}
}
