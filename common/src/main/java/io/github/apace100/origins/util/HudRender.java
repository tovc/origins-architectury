package io.github.apace100.origins.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record HudRender(boolean shouldRender, int barIndex, Identifier spriteLocation,
						@Nullable ConfiguredEntityCondition<?, ?> condition) {
	public static final Codec<HudRender> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("should_render", true).forGetter(HudRender::shouldRender),
			Codec.INT.optionalFieldOf("bar_index", 0).forGetter(HudRender::barIndex),
			Identifier.CODEC.optionalFieldOf("sprite_location", Origins.identifier("textures/gui/resource_bar.png")).forGetter(HudRender::spriteLocation),
			ConfiguredEntityCondition.CODEC.optionalFieldOf("condition").forGetter(x -> Optional.ofNullable(x.condition()))
	).apply(instance, (t1, t2, t3, t4) -> new HudRender(t1, t2, t3, t4.orElse(null))));

	public static final HudRender DONT_RENDER = new HudRender(false, 0, Origins.identifier("textures/gui/resource_bar.png"), null);

	public boolean shouldRender(PlayerEntity player) {
		return this.shouldRender() && ConfiguredEntityCondition.check(this.condition(), player);
	}
}
