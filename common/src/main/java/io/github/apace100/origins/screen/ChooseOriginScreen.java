package io.github.apace100.origins.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.origin.Impact;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.networking.ModPackets;
import io.github.apace100.origins.networking.packet.C2SChooseOriginPacket;
import io.github.apace100.origins.registry.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class ChooseOriginScreen extends OriginDisplayScreen {
	private static final int windowWidth = 176;
	private static final int windowHeight = 182;
	private final ArrayList<OriginLayer> layerList;
	private final int currentLayerIndex;
	private final List<Origin> originSelection;
	private final int maxSelection;
	private int currentOrigin = 0;

	private boolean showDirtBackground;

	private Origin randomOrigin;
	private MutableText randomOriginText;

	public ChooseOriginScreen(ArrayList<OriginLayer> layerList, int currentLayerIndex, boolean showDirtBackground) {
		super(new TranslatableText(Origins.MODID + ".screen.choose_origin"), windowWidth, windowHeight, 13);
		this.layerList = layerList;
		this.currentLayerIndex = currentLayerIndex;
		this.originSelection = new ArrayList<>(10);
		PlayerEntity player = MinecraftClient.getInstance().player;
		OriginLayer currentLayer = layerList.get(currentLayerIndex);
		Registry<Origin> origins = OriginsAPI.getOrigins();
		currentLayer.origins(player).flatMap(x -> origins.getOrEmpty(x).stream()).forEach(origin -> {
			if (origin.choosable()) {
				ItemStack displayItem = origin.displayItem();
				if (displayItem.getItem() == Items.PLAYER_HEAD && (!displayItem.hasTag() || !displayItem.getTag().contains("SkullOwner")))
					displayItem.getOrCreateTag().putString("SkullOwner", player.getDisplayName().getString());
				this.originSelection.add(origin);
			}
		});
		originSelection.sort(Origin.ORDER_COMPARATOR);
		int maxSelection = originSelection.size();
		if (currentLayer.allowRandom() && currentLayer.randomOrigins(player).findAny().isPresent())
			maxSelection += 1;
		this.maxSelection = maxSelection;
		if (maxSelection == 0) {
			openNextLayerScreen();
			return;
		}
		this.showDirtBackground = showDirtBackground;
	}

	private void openNextLayerScreen() {
		MinecraftClient.getInstance().openScreen(new WaitForNextLayerScreen(layerList, currentLayerIndex, showDirtBackground));
	}

	@Override
	protected Origin getCurrentOrigin() {
		if (currentOrigin == originSelection.size()) {
			if (randomOrigin == null) {
				initRandomOrigin();
			}
			return randomOrigin;
		}
		return originSelection.get(currentOrigin);
	}

	@Override
	protected OriginLayer getCurrentLayer() {
		return this.layerList.get(this.currentLayerIndex);
	}

	private void initRandomOrigin() {
		this.randomOrigin = Origin.builder().withIdentifier(OriginsAPI.identifier("random"))
				.withDisplay(ModItems.ORB_OF_ORIGIN.getDefaultStack()).withImpact(Impact.NONE)
				.withOrder(Integer.MAX_VALUE).withLoadingPriority(Integer.MAX_VALUE).build();;
		this.randomOriginText = new LiteralText("");
		Registry<Origin> origins = OriginsAPI.getOrigins();
		List<Origin> randoms = layerList.get(currentLayerIndex).randomOrigins(MinecraftClient.getInstance().player)
				.flatMap(x -> origins.getOrEmpty(x).stream())
				.sorted(Origin.ORDER_COMPARATOR).toList();
		for (Origin origin : randoms) {
			this.randomOriginText.append(origin.getName());
			this.randomOriginText.append(new LiteralText("\n"));
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (maxSelection == 0) {
			openNextLayerScreen();
			return;
		}
		this.renderBackground(matrices);
		this.renderOriginWindow(matrices, mouseX, mouseY);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public void renderBackground(MatrixStack matrices, int vOffset) {
		if (showDirtBackground) {
			super.renderBackgroundTexture(vOffset);
		} else {
			super.renderBackground(matrices, vOffset);
		}
	}

	@Override
	protected void init() {
		super.init();
		guiLeft = (this.width - windowWidth) / 2;
		guiTop = (this.height - windowHeight) / 2;
		addButton(new ButtonWidget(guiLeft - 40, this.height / 2 - 10, 20, 20, new LiteralText("<"), b -> {
			currentOrigin = (currentOrigin - 1 + maxSelection) % maxSelection;
			scrollPos = 0;
		}));
		addButton(new ButtonWidget(guiLeft + windowWidth + 20, this.height / 2 - 10, 20, 20, new LiteralText(">"), b -> {
			currentOrigin = (currentOrigin + 1) % maxSelection;
			scrollPos = 0;
		}));
		addButton(new ButtonWidget(guiLeft + windowWidth / 2 - 50, guiTop + windowHeight + 5, 100, 20, new TranslatableText(Origins.MODID + ".gui.select"), b -> {
			boolean isRandom = currentOrigin == originSelection.size();
			C2SChooseOriginPacket packet = new C2SChooseOriginPacket(OriginsAPI.getLayers().getId(layerList.get(currentLayerIndex)), isRandom ? null : OriginsAPI.getOrigins().getId(this.getCurrentOrigin()));
			ModPackets.CHANNEL.sendToServer(packet);
			openNextLayerScreen();
		}));
	}

	private void renderOriginWindow(MatrixStack matrices, int mouseX, int mouseY) {
		RenderSystem.enableBlend();
		renderWindowBackground(matrices, 16, 0);
		this.renderOriginContent(matrices, mouseX, mouseY);
		this.client.getTextureManager().bindTexture(WINDOW);
		this.drawTexture(matrices, guiLeft, guiTop, 0, 0, windowWidth, windowHeight);
		renderOriginName(matrices);
		this.client.getTextureManager().bindTexture(WINDOW);
		this.renderOriginImpact(matrices, mouseX, mouseY);
		Text title = new TranslatableText(Origins.MODID + ".gui.choose_origin.title", new TranslatableText(layerList.get(currentLayerIndex).name()));
		drawCenteredString(matrices, this.textRenderer, title.getString(), width / 2, guiTop - 15, 0xFFFFFF);
		RenderSystem.disableBlend();
	}

	private void renderOriginContent(MatrixStack matrices, int mouseX, int mouseY) {
		Origin origin = getCurrentOrigin();
		this.drawOrigin(this.getCurrentOrigin(), matrices, origin == this.randomOrigin, this.randomOriginText);
	}
}
