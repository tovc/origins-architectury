package io.github.apace100.origins.api.origin;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.Comparator;

public enum Impact{

	NONE(0, "none", Formatting.GRAY),
	LOW(1, "low", Formatting.GREEN),
	MEDIUM(2, "medium", Formatting.YELLOW),
	HIGH(3, "high", Formatting.RED);

	public static final Comparator<Impact> COMPARATOR = Comparator.comparingInt(Impact::getImpactValue);

	public static Impact getByValue(int impactValue) {
		return Impact.values()[impactValue];
	}
	private int impactValue;
	private String translationKey;
	private Formatting textStyle;

	Impact(int impactValue, String translationKey, Formatting textStyle) {
		this.translationKey = "conditionedOrigins.gui.impact." + translationKey;
		this.impactValue = impactValue;
		this.textStyle = textStyle;
	}

	public int getImpactValue() {
		return impactValue;
	}

	public String getTranslationKey() {
		return translationKey;
	}

	public Formatting getTextStyle() {
		return textStyle;
	}

	public TranslatableText getTextComponent() {
		return (TranslatableText) new TranslatableText(getTranslationKey()).formatted(getTextStyle());
	}
}
