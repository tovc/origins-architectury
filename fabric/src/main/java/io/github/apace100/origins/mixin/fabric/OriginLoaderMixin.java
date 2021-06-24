package io.github.apace100.origins.mixin.fabric;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.data.OriginLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(OriginLoader.class)
public abstract class OriginLoaderMixin implements IdentifiableResourceReloadListener {

	@Override
	public Identifier getFabricId() {
		return Origins.identifier("origin");
	}
}
