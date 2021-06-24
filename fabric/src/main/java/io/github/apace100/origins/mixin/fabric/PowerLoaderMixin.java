package io.github.apace100.origins.mixin.fabric;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.data.PowerLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PowerLoader.class)
public abstract class PowerLoaderMixin implements IdentifiableResourceReloadListener {

	@Override
	public Identifier getFabricId() {
		return Origins.identifier("power");
	}
}
