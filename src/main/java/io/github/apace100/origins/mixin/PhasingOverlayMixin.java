package io.github.apace100.origins.mixin;

import io.github.apace100.origins.OriginsClient;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class PhasingOverlayMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	protected abstract void renderConfusionOverlay(float p_109146_);

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F"))
	private void drawPhantomizedOverlay(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
		if (IPowerContainer.hasPower(this.minecraft.player, ApoliPowers.PHASING.get()) && !this.minecraft.player.hasEffect(MobEffects.CONFUSION))
			this.renderConfusionOverlay(OriginsClient.config.phantomizedOverlayStrength);
	}
}
