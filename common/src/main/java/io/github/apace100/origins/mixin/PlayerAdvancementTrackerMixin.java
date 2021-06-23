package io.github.apace100.origins.mixin;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginUpgrade;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {

	@Shadow
	private ServerPlayerEntity owner;

	@Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/PlayerAdvancementTracker;endTrackingCompleted(Lnet/minecraft/advancement/Advancement;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void checkOriginUpgrade(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> info, boolean bl, AdvancementProgress advancementProgress, boolean bl2) {
		if (advancementProgress.isDone()) {
			OriginsAPI.getComponent(this.owner).getOrigins().forEach((layer, o) -> {
				Optional<OriginUpgrade> upgrade = o.getUpgrade(advancement);
				if (upgrade.isPresent()) {
					try {
						Origin upgradeTo = OriginsAPI.getOrigins().get(upgrade.get().upgradeToOrigin());
						if (upgradeTo != null) {
							OriginComponent component = ModComponentsArchitectury.getOriginComponent(owner);
							component.setOrigin(layer, upgradeTo);
							component.sync();
							String announcement = upgrade.get().announcement();
							if (!announcement.isEmpty()) {
								owner.sendMessage(new TranslatableText(announcement).formatted(Formatting.GOLD), false);
							}
						}
					} catch (IllegalArgumentException e) {
						Origins.LOGGER.error("Could not perform Origins upgrade from {} to {}, as the upgrade origin did not exist!", OriginsAPI.getOrigins().getId(o).toString(), upgrade.get().upgradeToOrigin().toString());
					}
				}
			});
		}
	}
}
