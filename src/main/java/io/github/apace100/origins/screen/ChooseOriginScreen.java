package io.github.apace100.origins.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.networking.ModPackets;
import io.github.apace100.origins.origin.Impact;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModItems;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.text.*;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.ArrayList;
import java.util.List;

public class ChooseOriginScreen extends Screen {

	private static final ResourceLocation WINDOW = new ResourceLocation(Origins.MODID, "textures/gui/choose_origin.png");

	private ArrayList<OriginLayer> layerList;
	private int currentLayerIndex = 0;
	private int currentOrigin = 0;
	private List<Origin> originSelection;
	private int maxSelection = 0;
	private static final int windowWidth = 176;
	private static final int windowHeight = 182;
	private int scrollPos = 0;
	private int currentMaxScroll = 0;
	private int border = 13;
	
	private int guiTop, guiLeft;

	private boolean showDirtBackground;

	private Origin randomOrigin;
	private MutableComponent randomOriginText;
	
	public ChooseOriginScreen(ArrayList<OriginLayer> layerList, int currentLayerIndex, boolean showDirtBackground) {
		super(new TranslatableComponent(Origins.MODID + ".screen.choose_origin"));
		this.layerList = layerList;
		this.currentLayerIndex = currentLayerIndex;
		this.originSelection = new ArrayList<>(10);
		Player player = Minecraft.getInstance().player;
		OriginLayer currentLayer = layerList.get(currentLayerIndex);
		List<ResourceLocation> originIdentifiers = currentLayer.getOrigins(player);
		originIdentifiers.forEach(originId -> {
			Origin origin = OriginRegistry.get(originId);
			if(origin.isChoosable()) {
				ItemStack displayItem = origin.getDisplayItem();
				if(displayItem.getItem() == Items.PLAYER_HEAD) {
					if(!displayItem.hasTag() || !displayItem.getTag().contains("SkullOwner")) {
						displayItem.getOrCreateTag().putString("SkullOwner", player.getDisplayName().getString());
					}
				}
				this.originSelection.add(origin);
			}
		});
		originSelection.sort((a, b) -> {
			int impDelta = a.getImpact().getImpactValue() - b.getImpact().getImpactValue();
			return impDelta == 0 ? a.getOrder() - b.getOrder() : impDelta;
		});
		maxSelection = originSelection.size();
		if(currentLayer.isRandomAllowed() && currentLayer.getRandomOrigins(player).size() > 0) {
			maxSelection += 1;
		}
		if(maxSelection == 0) {
			openNextLayerScreen();
			return;
		}
		this.showDirtBackground = showDirtBackground;
	}

	private void openNextLayerScreen() {
		Minecraft.getInstance().setScreen(new WaitForNextLayerScreen(layerList, currentLayerIndex, showDirtBackground));
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	protected void init() {
		super.init();
		guiLeft = (this.width - windowWidth) / 2;
        guiTop = (this.height - windowHeight) / 2;
		addRenderableWidget(new Button(guiLeft - 40,this.height / 2 - 10, 20, 20, new TextComponent("<"), b -> {
        	currentOrigin = (currentOrigin - 1 + maxSelection) % maxSelection;
        	scrollPos = 0;
        }));
		addRenderableWidget(new Button(guiLeft + windowWidth + 20, this.height / 2 - 10, 20, 20, new TextComponent(">"), b -> {
        	currentOrigin = (currentOrigin + 1) % maxSelection;
        	scrollPos = 0;
        }));
		addRenderableWidget(new Button(guiLeft + windowWidth / 2 - 50, guiTop + windowHeight + 5, 100, 20, new TranslatableComponent(Origins.MODID + ".gui.select"), b -> {
			FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
			if(currentOrigin == originSelection.size()) {
				buf.writeUtf(layerList.get(currentLayerIndex).getIdentifier().toString());
				ClientPlayNetworking.send(ModPackets.CHOOSE_RANDOM_ORIGIN, buf);
			} else {
				buf.writeUtf(getCurrentOrigin().getIdentifier().toString());
				buf.writeUtf(layerList.get(currentLayerIndex).getIdentifier().toString());
				ClientPlayNetworking.send(ModPackets.CHOOSE_ORIGIN, buf);
			}
			openNextLayerScreen();
        }));
	}

	private Origin getCurrentOrigin() {
		if(currentOrigin == originSelection.size()) {
			if(randomOrigin == null) {
				initRandomOrigin();
			}
			return randomOrigin;
		}
		return originSelection.get(currentOrigin);
	}

	private void initRandomOrigin() {
		this.randomOrigin = new Origin(Origins.identifier("random"), new ItemStack(ModItems.ORB_OF_ORIGIN), Impact.NONE, -1, Integer.MAX_VALUE);
		this.randomOriginText = new TextComponent("");
		List<ResourceLocation> randoms = layerList.get(currentLayerIndex).getRandomOrigins(Minecraft.getInstance().player);
		randoms.sort((ia, ib) -> {
			Origin a = OriginRegistry.get(ia);
			Origin b = OriginRegistry.get(ib);
			int impDelta = a.getImpact().getImpactValue() - b.getImpact().getImpactValue();
			return impDelta == 0 ? a.getOrder() - b.getOrder() : impDelta;
		});
		for(ResourceLocation id : randoms) {
			this.randomOriginText.append(OriginRegistry.get(id).getName());
			this.randomOriginText.append(new TextComponent("\n"));
		}
	}

	@Override
	public void renderBackground(PoseStack matrices, int vOffset) {
		if(showDirtBackground) {
			super.renderDirtBackground(vOffset);
		} else {
			super.renderBackground(matrices, vOffset);
		}
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
		if(maxSelection == 0) {
			openNextLayerScreen();
			return;
		}
		this.renderBackground(matrices);
		this.renderOriginWindow(matrices, mouseX, mouseY);
		super.render(matrices, mouseX, mouseY, delta);
	}

	private void renderOriginWindow(PoseStack matrices, int mouseX, int mouseY) {
		RenderSystem.enableBlend();
		renderWindowBackground(matrices, 16, 0);
		this.renderOriginContent(matrices, mouseX, mouseY);
		RenderSystem.setShaderTexture(0, WINDOW);
		this.blit(matrices, guiLeft, guiTop, 0, 0, windowWidth, windowHeight);
		renderOriginName(matrices);
		RenderSystem.setShaderTexture(0, WINDOW);
		this.renderOriginImpact(matrices, mouseX, mouseY);
		Component title = new TranslatableComponent(Origins.MODID + ".gui.choose_origin.title", new TranslatableComponent(layerList.get(currentLayerIndex).getTranslationKey()));
		this.drawCenteredString(matrices, this.font, title.getString(), width / 2, guiTop - 15, 0xFFFFFF);
		RenderSystem.disableBlend();
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
		FormattedText originName = font.substrByWidth(getCurrentOrigin().getName(), windowWidth - 36);
		this.drawString(matrices, font, originName.getString(), guiLeft + 39, guiTop + 19, 0xFFFFFF);
		ItemStack is = getCurrentOrigin().getDisplayItem();
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
		Origin origin = getCurrentOrigin();
		int x = guiLeft + 18;
		int y = guiTop + 50;
		int startY = y;
		int endY = y - 72 + windowHeight;
		y -= scrollPos;
		
		Component orgDesc = origin.getDescription();
		List<FormattedCharSequence> descLines = font.split(orgDesc, windowWidth - 36);
		for(FormattedCharSequence line : descLines) {
			if(y >= startY - 18 && y <= endY + 12) {
				font.draw(matrices, line, x + 2, y - 6, 0xCCCCCC);
			}
			y += 12;
		}

		if(origin == randomOrigin) {
			List<FormattedCharSequence> drawLines = font.split(randomOriginText, windowWidth - 36);
			for(FormattedCharSequence line : drawLines) {
				y += 12;
				if(y >= startY - 24 && y <= endY + 12) {
					font.draw(matrices, line, x + 2, y, 0xCCCCCC);
				}
			}
			y += 14;
		} else {
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
		}
		y += scrollPos;
		currentMaxScroll = y - windowHeight - 15;
		if(currentMaxScroll < 0) {
			currentMaxScroll = 0;
		}
	}
}
