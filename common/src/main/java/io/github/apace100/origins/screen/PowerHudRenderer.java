package io.github.apace100.origins.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.OriginsClient;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.IHudRenderedPower;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.util.HudRender;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PowerHudRenderer extends DrawableHelper implements GameHudRender {

	@Override
	@Environment(EnvType.CLIENT)
	public void render(MatrixStack matrices, float delta) {
		MinecraftClient client = MinecraftClient.getInstance();
		PlayerEntity player = client.player;
		OriginComponent component = ModComponentsArchitectury.getOriginComponent(player);
		if (component.hasAllOrigins()) {
			int x = client.getWindow().getScaledWidth() / 2 + 20 + OriginsClient.config.xOffset;
			int y = client.getWindow().getScaledHeight() - 47 + OriginsClient.config.yOffset;
			Entity vehicle = player.getVehicle();
			if (vehicle instanceof LivingEntity && ((LivingEntity) vehicle).getMaxHealth() > 20F) {
				y -= 8;
			}
			if (player.isSubmergedIn(FluidTags.WATER) || player.getAir() < player.getMaxAir()) {
				y -= 8;
			}
			int barWidth = 71;
			int barHeight = 5;
			int iconSize = 8;
			List<ConfiguredPower<?, ?>> hudPowers = component.getPowers().stream().filter(p -> p.getFactory() instanceof IHudRenderedPower).sorted(
					Comparator.comparing(hudRenderedA -> hudRenderedA.getRenderSettings(player).map(HudRender::spriteLocation).orElseGet(() -> Origins.identifier("empty")))
			).collect(Collectors.toList());
			Identifier lastLocation = null;
			RenderSystem.color3f(1f, 1f, 1f);
			for (ConfiguredPower<?, ?> hudPower : hudPowers) {
				HudRender render = hudPower.getRenderSettings(player).orElseThrow();
				if (render.shouldRender(player) && hudPower.shouldRender(player).orElse(false)) {
					Identifier currentLocation = render.spriteLocation();
					if (currentLocation != lastLocation) {
						client.getTextureManager().bindTexture(currentLocation);
						lastLocation = currentLocation;
					}
					drawTexture(matrices, x, y, 0, 0, barWidth, barHeight);
					int v = 10 + render.barIndex() * 10;
					int w = (int) (hudPower.getFill(player).orElse(0.0F) * barWidth);
					drawTexture(matrices, x, y, 0, v, w, barHeight);
					setZOffset(getZOffset() + 1);
					drawTexture(matrices, x - iconSize - 2, y - 2, 73, v - 2, iconSize, iconSize);
					setZOffset(getZOffset() - 1);
					y -= 8;
				}
			}
		}
	}
}
