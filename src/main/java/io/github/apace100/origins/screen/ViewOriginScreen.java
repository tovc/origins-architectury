package io.github.apace100.origins.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.OriginsClient;
import io.github.apace100.origins.origin.Impact;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.text.*;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ViewOriginScreen extends Screen {

	private static final ResourceLocation WINDOW = new ResourceLocation(Origins.MODID, "textures/gui/choose_origin.png");

	private ArrayList<Tuple<OriginLayer, Origin>> originLayers;
	private int currentLayer = 0;
	private static final int windowWidth = 176;
	private static final int windowHeight = 182;
	private int scrollPos = 0;
	private int currentMaxScroll = 0;
	private int border = 13;

	private Button chooseOriginButton;

	private int guiTop, guiLeft;

	public ViewOriginScreen() {
		super(new TranslatableComponent(Origins.MODID + ".screen.view_origin"));
		HashMap<OriginLayer, Origin> origins = ModComponents.ORIGIN.get(Minecraft.getInstance().player).getOrigins();
		originLayers = new ArrayList<>(origins.size());
		Player player = Minecraft.getInstance().player;
		origins.forEach((layer, origin) -> {
			ItemStack displayItem = origin.getDisplayItem();
			if(displayItem.getItem() == Items.PLAYER_HEAD) {
				if(!displayItem.hasTag() || !displayItem.getTag().contains("SkullOwner")) {
					displayItem.getOrCreateTag().putString("SkullOwner", player.getDisplayName().getString());
				}
			}
			if(origin != Origin.EMPTY || layer.getOriginOptionCount(player) > 0) {
				originLayers.add(new Tuple<>(layer, origin));
			}
		});
		originLayers.sort(Comparator.comparing(Tuple::getA));
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

	@Override
	protected void init() {
		super.init();
		guiLeft = (this.width - windowWidth) / 2;
        guiTop = (this.height - windowHeight) / 2;
        if(originLayers.size() > 0 && OriginsClient.isServerRunningOrigins) {
			addRenderableWidget(chooseOriginButton = new Button(guiLeft + windowWidth / 2 - 50, guiTop + windowHeight - 40, 100, 20, new TranslatableComponent(Origins.MODID + ".gui.choose"), b -> {
				Minecraft.getInstance().setScreen(new ChooseOriginScreen(Lists.newArrayList(originLayers.get(currentLayer).getA()), 0, false));
			}));
			Player player = Minecraft.getInstance().player;
			chooseOriginButton.active = chooseOriginButton.visible = originLayers.get(currentLayer).getB() == Origin.EMPTY && originLayers.get(currentLayer).getA().getOriginOptionCount(player) > 0;
			addRenderableWidget(new Button(guiLeft - 40,this.height / 2 - 10, 20, 20, new TextComponent("<"), b -> {
				currentLayer = (currentLayer - 1 + originLayers.size()) % originLayers.size();
				chooseOriginButton.active = chooseOriginButton.visible = originLayers.get(currentLayer).getB() == Origin.EMPTY && originLayers.get(currentLayer).getA().getOriginOptionCount(player) > 0;
				scrollPos = 0;
			}));
			addRenderableWidget(new Button(guiLeft + windowWidth + 20, this.height / 2 - 10, 20, 20, new TextComponent(">"), b -> {
				currentLayer = (currentLayer + 1) % originLayers.size();
				chooseOriginButton.active = chooseOriginButton.visible = originLayers.get(currentLayer).getB() == Origin.EMPTY && originLayers.get(currentLayer).getA().getOriginOptionCount(player) > 0;
				scrollPos = 0;
			}));
		}
		addRenderableWidget(new Button(guiLeft + windowWidth / 2 - 50, guiTop + windowHeight + 5, 100, 20, new TranslatableComponent(Origins.MODID + ".gui.close"), b -> {
			Minecraft.getInstance().setScreen(null);
        }));
	}
	
	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.renderOriginWindow(matrices, mouseX, mouseY);
		super.render(matrices, mouseX, mouseY, delta);
	}

	private void renderOriginWindow(PoseStack matrices, int mouseX, int mouseY) {
		RenderSystem.enableBlend();
		boolean hasLayer = originLayers.size() > 0;
		if(hasLayer && OriginsClient.isServerRunningOrigins) {
			renderWindowBackground(matrices, 16, 0);
			this.renderOriginContent(matrices, mouseX, mouseY);
			RenderSystem.setShaderTexture(0, WINDOW);
			this.blit(matrices, guiLeft, guiTop, 0, 0, windowWidth, windowHeight);
			renderOriginName(matrices);
			RenderSystem.setShaderTexture(0, WINDOW);
			this.renderOriginImpact(matrices, mouseX, mouseY);
			Component title = new TranslatableComponent(Origins.MODID + ".gui.view_origin.title", new TranslatableComponent(originLayers.get(currentLayer).getA().getTranslationKey()));
			drawCenteredString(matrices, this.font, title.getString(), width / 2, guiTop - 15, 0xFFFFFF);
		} else {
			if(OriginsClient.isServerRunningOrigins) {
				drawCenteredString(matrices, this.font, new TranslatableComponent(Origins.MODID + ".gui.view_origin.empty").getString(), width / 2, guiTop + 15, 0xFFFFFF);
			} else {
				drawCenteredString(matrices, this.font, new TranslatableComponent(Origins.MODID + ".gui.view_origin.not_installed").getString(), width / 2, guiTop + 15, 0xFFFFFF);
			}
		}
		RenderSystem.disableBlend();
	}

	private Origin getCurrentOrigin() {
		return originLayers.get(currentLayer).getB();
	}
	
	private void renderOriginImpact(PoseStack matrices, int mouseX, int mouseY) {
		Impact impact = getCurrentOrigin().getImpact();
		int impactValue = impact.getImpactValue();
		int wOffset = impactValue * 8;
		for(int i = 0; i < 3; i++) {
			if(i < impactValue) {
				this.blit(matrices, guiLeft + 128 + i * 10, guiTop + 19, windowWidth + wOffset, 16, 8, 8);
			} else {
				this.blit(matrices, guiLeft + 128 + i * 10, guiTop + 19, windowWidth, 16, 8, 8);
			}
		}
		if(mouseX >= guiLeft + 128 && mouseX <= guiLeft + 158
		&& mouseY >= guiTop + 19 && mouseY <= guiTop + 27) {
			TranslatableComponent ttc = (TranslatableComponent) new TranslatableComponent(Origins.MODID + ".gui.impact.impact").append(": ").append(impact.getTextComponent());
			this.renderTooltip(matrices, ttc, mouseX, mouseY);
		}
	}
	
	private void renderOriginName(PoseStack matrices) {
		Origin origin = getCurrentOrigin();
		FormattedText originName = font.substrByWidth(origin == Origin.EMPTY ? new TranslatableComponent(originLayers.get(currentLayer).getA().getMissingOriginNameTranslationKey()) : origin.getName(), windowWidth - 36);
		this.drawString(matrices, font, originName.getString(), guiLeft + 39, guiTop + 19, 0xFFFFFF);
		ItemStack is = origin.getDisplayItem();
		this.itemRenderer.renderAndDecorateFakeItem(is, guiLeft + 15, guiTop + 15);
	}
	
	private void renderWindowBackground(PoseStack matrices, int offsetYStart, int offsetYEnd) {
		int endX = guiLeft + windowWidth - border;
		int endY = guiTop + windowHeight - border;
		RenderSystem.setShaderTexture(0, WINDOW);
		for(int x = guiLeft; x < endX; x += 16) {
			for(int y = guiTop + offsetYStart; y < endY + offsetYEnd; y += 16) {
				this.blit(matrices, x, y, windowWidth, 0, Math.max(16, endX - x), Math.max(16, endY + offsetYEnd - y));
			}
		}
	}
	
	@Override
	public boolean mouseScrolled(double x, double y, double z) {
		boolean retValue = super.mouseScrolled(x, y, z);
		int np = this.scrollPos - (int)z * 4;
		this.scrollPos = np < 0 ? 0 : Math.min(np, this.currentMaxScroll);
		return retValue;
	}

	private void renderOriginContent(PoseStack matrices, int mouseX, int mouseY) {
		int x = guiLeft + 18;
		int y = guiTop + 50;
		int startY = y;
		int endY = y - 72 + windowHeight;
		y -= scrollPos;

		Origin origin = getCurrentOrigin();


		Component orgDesc = origin.getDescription();
		if(origin == Origin.EMPTY) {
			orgDesc = new TranslatableComponent(originLayers.get(currentLayer).getA().getMissingOriginDescriptionTranslationKey());
		}
		List<FormattedCharSequence> descLines = font.split(orgDesc, windowWidth - 36);
		for(FormattedCharSequence line : descLines) {
			if(y >= startY - 18 && y <= endY + 12) {
				font.draw(matrices, line, x + 2, y - 6, 0xCCCCCC);
			}
			y += 12;
		}
		if(origin == Origin.EMPTY) {
			return;
		}
		for(PowerType<?> p : origin.getPowerTypes()) {
			if(p.isHidden()) {
				continue;
			}
			FormattedCharSequence name = Language.getInstance().getVisualOrder(font.substrByWidth(p.getName().withStyle(ChatFormatting.UNDERLINE), windowWidth - 36));
			Component desc = p.getDescription();
			List<FormattedCharSequence> drawLines = font.split(desc, windowWidth - 36);
			if(y >= startY - 24 && y <= endY + 12) {
				font.draw(matrices, name, x, y, 0xFFFFFF);
			}
			for(FormattedCharSequence line : drawLines) {
				y += 12;
				if(y >= startY - 24 && y <= endY + 12) {
					font.draw(matrices, line, x + 2, y, 0xCCCCCC);
				}
			}

			y += 14;
			
		}
		y += scrollPos;
		currentMaxScroll = y - windowHeight - 15;
		if(currentMaxScroll < 0) {
			currentMaxScroll = 0;
		}
	}
}
