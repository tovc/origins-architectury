package io.github.apace100.origins.mixin.fabric;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.data.OriginLayerLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(OriginLayerLoader.class)
public abstract class OriginLayerLoaderMixin implements IdentifiableResourceReloadListener {

	@Override
	public Identifier getFabricId() {
		return Origins.identifier("origin_layers");
	}
}
