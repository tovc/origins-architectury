package io.github.apace100.origins.screen;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.OriginsAPI;
import io.github.apace100.origins.api.origin.Impact;
import io.github.apace100.origins.api.origin.Origin;
import io.github.apace100.origins.api.origin.OriginLayer;
import io.github.apace100.origins.api.power.PowerData;
import io.github.apace100.origins.api.power.configuration.ConfiguredPower;
import io.github.apace100.origins.registry.ModOrigins;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.registry.Registry;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class OriginDisplayScreen extends Screen {
	protected static final Identifier WINDOW = new Identifier(Origins.MODID, "textures/gui/choose_origin.png");

	protected final int windowWidth;
	protected final int windowHeight;
	private final int border;
	protected int guiTop;
	protected int guiLeft;
	protected int scrollPos;
	protected int currentMaxScroll;

	protected OriginDisplayScreen(Text text, int width, int height, int border) {
		super(text);
		this.windowWidth = width;
		this.windowHeight = height;
		this.border = border;
	}

	@Override
	protected void init() {
		super.init();
		guiLeft = (this.width - windowWidth) / 2;
		guiTop = (this.height - windowHeight) / 2;
	}
	protected abstract Origin getCurrentOrigin();
	protected abstract OriginLayer getCurrentLayer();

	protected void drawOrigin(Origin origin, MatrixStack matrices, boolean isRandom, Text randomOriginText) {
		int x = guiLeft + 18;
		int y = guiTop + 50;
		int startY = y;
		int endY = y - 72 + windowHeight;
		y -= scrollPos;

		Text orgDesc = origin.getDescription();
		if (origin == ModOrigins.EMPTY)
			orgDesc = new TranslatableText(this.getCurrentLayer().missingName());
		List<OrderedText> descLines = textRenderer.wrapLines(orgDesc, windowWidth - 36);
		for (OrderedText line : descLines) {
			if (y >= startY - 18 && y <= endY + 12) {
				textRenderer.draw(matrices, line, x + 2, y - 6, 0xCCCCCC);
			}
			y += 12;
		}
		if (origin == ModOrigins.EMPTY)
			return;
		if (isRandom && randomOriginText != null) {
			List<OrderedText> drawLines = textRenderer.wrapLines(randomOriginText, windowWidth - 36);
			for (OrderedText line : drawLines) {
				y += 12;
				if (y >= startY - 24 && y <= endY + 12) {
					textRenderer.draw(matrices, line, x + 2, y, 0xCCCCCC);
				}
			}
			y += 14;
		} else {
			Registry<ConfiguredPower<?, ?>> powers = OriginsAPI.getPowers();
			for (Identifier p : origin.powers()) {
				ConfiguredPower<?, ?> configuredPower = powers.get(p);
				if (configuredPower == null) {
					Origins.LOGGER.error("Unregistered power {} was found.", p);
					continue;
				}
				PowerData data = configuredPower.getData();
				if (data.hidden()) {
					continue;
				}
				OrderedText name = Language.getInstance().reorder(textRenderer.trimToWidth(data.getName().formatted(Formatting.UNDERLINE), windowWidth - 36));
				Text desc = data.getDescription();
				List<OrderedText> drawLines = textRenderer.wrapLines(desc, windowWidth - 36);
				if (y >= startY - 24 && y <= endY + 12) {
					textRenderer.draw(matrices, name, x, y, 0xFFFFFF);
				}
				for (OrderedText line : drawLines) {
					y += 12;
					if (y >= startY - 24 && y <= endY + 12) {
						textRenderer.draw(matrices, line, x + 2, y, 0xCCCCCC);
					}
				}

				y += 14;
			}
		}
		y += scrollPos;
		currentMaxScroll = y - windowHeight - 15;
		if (currentMaxScroll < 0) {
			currentMaxScroll = 0;
		}
	}


	protected void renderWindowBackground(MatrixStack matrices, int offsetYStart, int offsetYEnd) {
		int endX = guiLeft + this.windowWidth - border;
		int endY = guiTop + this.windowHeight - border;
		this.client.getTextureManager().bindTexture(WINDOW);
		for (int x = guiLeft; x < endX; x += 16) {
			for (int y = guiTop + offsetYStart; y < endY + offsetYEnd; y += 16) {
				this.drawTexture(matrices, x, y, windowWidth, 0, Math.max(16, endX - x), Math.max(16, endY + offsetYEnd - y));
			}
		}
	}

	protected void renderOriginImpact(MatrixStack matrices, int mouseX, int mouseY) {
		Impact impact = this.getCurrentOrigin().impact();
		int impactValue = impact.getImpactValue();
		int wOffset = impactValue * 8;
		for (int i = 0; i < 3; i++) {
			if (i < impactValue) {
				this.drawTexture(matrices, guiLeft + 128 + i * 10, guiTop + 19, windowWidth + wOffset, 16, 8, 8);
			} else {
				this.drawTexture(matrices, guiLeft + 128 + i * 10, guiTop + 19, windowWidth, 16, 8, 8);
			}
		}
		if (mouseX >= guiLeft + 128 && mouseX <= guiLeft + 158
			&& mouseY >= guiTop + 19 && mouseY <= guiTop + 27) {
			TranslatableText ttc = (TranslatableText) new TranslatableText(Origins.MODID + ".gui.impact.impact").append(": ").append(impact.getTextComponent());
			this.renderTooltip(matrices, ttc, mouseX, mouseY);
		}
	}

	protected void renderOriginName(MatrixStack matrices) {
		Origin origin = this.getCurrentOrigin();
		StringVisitable originName = textRenderer.trimToWidth(origin == ModOrigins.EMPTY ? new TranslatableText(this.getCurrentLayer().missingName()) : origin.getName(), windowWidth - 36);
		drawStringWithShadow(matrices, textRenderer, originName.getString(), guiLeft + 39, guiTop + 19, 0xFFFFFF);
		ItemStack is = origin.displayItem();
		this.itemRenderer.renderInGui(is, guiLeft + 15, guiTop + 15);
	}

	@Override
	public boolean mouseScrolled(double x, double y, double z) {
		boolean retValue = super.mouseScrolled(x, y, z);
		int np = this.scrollPos - (int) z * 4;
		this.scrollPos = np < 0 ? 0 : Math.min(np, this.currentMaxScroll);
		return retValue;
	}
}
