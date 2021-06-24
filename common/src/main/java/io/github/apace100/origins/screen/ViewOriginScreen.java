package io.github.apace100.origins.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.OriginsClient;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.registry.ModOrigins;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class ViewOriginScreen extends OriginDisplayScreen {

	private static final int windowWidth = 176;
	private static final int windowHeight = 182;
	private ArrayList<Pair<OriginLayer, Origin>> originLayers;
	private int currentLayer = 0;
	private ButtonWidget chooseOriginButton;

	public ViewOriginScreen() {
		super(new TranslatableText(Origins.MODID + ".screen.view_origin"), windowWidth, windowHeight, 13);
		Map<OriginLayer, Origin> origins = OriginsAPI.getComponent(MinecraftClient.getInstance().player).getOrigins();
		originLayers = new ArrayList<>(origins.size());
		PlayerEntity player = MinecraftClient.getInstance().player;
		origins.forEach((layer, origin) -> {
			if (origin.displayItem().getItem() == Items.PLAYER_HEAD) {
				origin.displayItem().getOrCreateTag().putString("SkullOwner", player.getDisplayName().getString());
			}
			if (origin != ModOrigins.EMPTY || layer.optionCount(player) > 0) {
				originLayers.add(new Pair<>(layer, origin));
			}
		});
		originLayers.sort(Comparator.comparing(Pair::getLeft));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.renderOriginWindow(matrices, mouseX, mouseY);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

	@Override
	protected void init() {
		super.init();
		if (originLayers.size() > 0) {
			addButton(chooseOriginButton = new ButtonWidget(guiLeft + windowWidth / 2 - 50, guiTop + windowHeight - 40, 100, 20, new TranslatableText(Origins.MODID + ".gui.choose"),
					b -> MinecraftClient.getInstance().openScreen(new ChooseOriginScreen(Lists.newArrayList(originLayers.get(currentLayer).getLeft()), 0, false))));
			PlayerEntity player = MinecraftClient.getInstance().player;
			chooseOriginButton.active = chooseOriginButton.visible = originLayers.get(currentLayer).getRight() == ModOrigins.EMPTY && originLayers.get(currentLayer).getLeft().optionCount(player) > 0;
			addButton(new ButtonWidget(guiLeft - 40, this.height / 2 - 10, 20, 20, new LiteralText("<"), b -> {
				currentLayer = (currentLayer - 1 + originLayers.size()) % originLayers.size();
				chooseOriginButton.active = chooseOriginButton.visible = originLayers.get(currentLayer).getRight() == ModOrigins.EMPTY && originLayers.get(currentLayer).getLeft().optionCount(player) > 0;
				scrollPos = 0;
			}));
			addButton(new ButtonWidget(guiLeft + windowWidth + 20, this.height / 2 - 10, 20, 20, new LiteralText(">"), b -> {
				currentLayer = (currentLayer + 1) % originLayers.size();
				chooseOriginButton.active = chooseOriginButton.visible = originLayers.get(currentLayer).getRight() == ModOrigins.EMPTY && originLayers.get(currentLayer).getLeft().optionCount(player) > 0;
				scrollPos = 0;
			}));
		}
		addButton(new ButtonWidget(guiLeft + windowWidth / 2 - 50, guiTop + windowHeight + 5, 100, 20, new TranslatableText(Origins.MODID + ".gui.close"), b ->
				MinecraftClient.getInstance().openScreen(null)));
	}

	@Override
	protected Origin getCurrentOrigin() {
		return originLayers.get(currentLayer).getRight();
	}

	@Override
	public OriginLayer getCurrentLayer() {
		return this.originLayers.get(this.currentLayer).getLeft();
	}

	private void renderOriginWindow(MatrixStack matrices, int mouseX, int mouseY) {
		RenderSystem.enableBlend();
		boolean hasLayer = originLayers.size() > 0;
		if (hasLayer && OriginsClient.isServerRunningOrigins) {
			renderWindowBackground(matrices, 16, 0);
			this.renderOriginContent(matrices, mouseX, mouseY);
			this.client.getTextureManager().bindTexture(WINDOW);
			this.drawTexture(matrices, guiLeft, guiTop, 0, 0, windowWidth, windowHeight);
			renderOriginName(matrices);
			this.client.getTextureManager().bindTexture(WINDOW);
			this.renderOriginImpact(matrices, mouseX, mouseY);
			Text title = new TranslatableText(Origins.MODID + ".gui.view_origin.title", new TranslatableText(originLayers.get(currentLayer).getLeft().name()));
			drawCenteredString(matrices, this.textRenderer, title.getString(), width / 2, guiTop - 15, 0xFFFFFF);
		} else {
			if (OriginsClient.isServerRunningOrigins) {
				drawCenteredString(matrices, this.textRenderer, new TranslatableText(Origins.MODID + ".gui.view_origin.empty").getString(), width / 2, guiTop + 15, 0xFFFFFF);
			} else {
				drawCenteredString(matrices, this.textRenderer, new TranslatableText(Origins.MODID + ".gui.view_origin.not_installed").getString(), width / 2, guiTop + 15, 0xFFFFFF);
			}
		}
		RenderSystem.disableBlend();
	}

	private void renderOriginContent(MatrixStack matrices, int mouseX, int mouseY) {
		Origin origin = getCurrentOrigin();
		this.drawOrigin(origin, matrices, false, null);
	}
}
