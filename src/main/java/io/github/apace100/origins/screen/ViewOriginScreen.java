package io.github.apace100.origins.screen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.origins.Origins;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class ViewOriginScreen extends OriginDisplayScreen {

	private static final ResourceLocation WINDOW = new ResourceLocation(Origins.MODID, "textures/gui/choose_origin.png");

	private final ArrayList<Tuple<OriginLayer, Origin>> originLayers;
	private int currentLayer = 0;

	private Button chooseOriginButton;

	public ViewOriginScreen() {
		super(new TranslatableComponent(Origins.MODID + ".screen.view_origin"));
		Map<OriginLayer, Origin> origins = IOriginContainer.get(Minecraft.getInstance().player).map(IOriginContainer::getOrigins).orElseGet(ImmutableMap::of);
		this.originLayers = new ArrayList<>(origins.size());
		Player player = Minecraft.getInstance().player;
		origins.forEach((layer, origin) -> {
			ItemStack displayItem = origin.getIcon().copy();
			if (displayItem.getItem() == Items.PLAYER_HEAD) {
				if (!displayItem.hasTag() || !displayItem.getTag().contains("SkullOwner")) {
					displayItem.getOrCreateTag().putString("SkullOwner", player.getDisplayName().getString());
				}
			}
			if (origin != Origin.EMPTY || layer.getOriginOptionCount(player) > 0) {
				this.originLayers.add(new Tuple<>(layer, origin));
			}
		});
		this.originLayers.sort(Comparator.comparing(Tuple::getA));
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

	@Override
	protected void init() {
		super.init();
		this.guiLeft = (this.width - windowWidth) / 2;
		this.guiTop = (this.height - windowHeight) / 2;
		if (this.originLayers.size() > 0) {
			this.addRenderableWidget(this.chooseOriginButton = new Button(this.guiLeft + windowWidth / 2 - 50, this.guiTop + windowHeight - 40, 100, 20, new TranslatableComponent(Origins.MODID + ".gui.choose"), b -> {
				Minecraft.getInstance().setScreen(new ChooseOriginScreen(Lists.newArrayList(this.originLayers.get(this.currentLayer).getA()), 0, false));
			}));
			Player player = Minecraft.getInstance().player;
			this.chooseOriginButton.active = this.chooseOriginButton.visible = this.originLayers.get(this.currentLayer).getB() == Origin.EMPTY && this.originLayers.get(this.currentLayer).getA().getOriginOptionCount(player) > 0;
			this.addRenderableWidget(new Button(this.guiLeft - 40, this.height / 2 - 10, 20, 20, new TextComponent("<"), b -> {
				this.currentLayer = (this.currentLayer - 1 + this.originLayers.size()) % this.originLayers.size();
				this.chooseOriginButton.active = this.chooseOriginButton.visible = this.originLayers.get(this.currentLayer).getB() == Origin.EMPTY && this.originLayers.get(this.currentLayer).getA().getOriginOptionCount(player) > 0;
				this.scrollPos = 0;
			}));
			this.addRenderableWidget(new Button(this.guiLeft + windowWidth + 20, this.height / 2 - 10, 20, 20, new TextComponent(">"), b -> {
				this.currentLayer = (this.currentLayer + 1) % this.originLayers.size();
				this.chooseOriginButton.active = this.chooseOriginButton.visible = this.originLayers.get(this.currentLayer).getB() == Origin.EMPTY && this.originLayers.get(this.currentLayer).getA().getOriginOptionCount(player) > 0;
				this.scrollPos = 0;
			}));
		}
		this.addRenderableWidget(new Button(this.guiLeft + windowWidth / 2 - 50, this.guiTop + windowHeight + 5, 100, 20, new TranslatableComponent(Origins.MODID + ".gui.close"), b -> {
			Minecraft.getInstance().setScreen(null);
		}));
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.renderOriginWindow(matrices, mouseX, mouseY);
		super.render(matrices, mouseX, mouseY, delta);
	}

	protected Origin getCurrentOrigin() {
		return this.originLayers.get(this.currentLayer).getB();
	}

	@Override
	protected OriginLayer getCurrentLayer() {
		return this.originLayers.get(this.currentLayer).getA();
	}


	@Override
	public boolean mouseScrolled(double x, double y, double z) {
		boolean retValue = super.mouseScrolled(x, y, z);
		int np = this.scrollPos - (int) z * 4;
		this.scrollPos = np < 0 ? 0 : Math.min(np, this.currentMaxScroll);
		return retValue;
	}
}
