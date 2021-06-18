package io.github.apace100.origins.power.configuration.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.power.configuration.ConfiguredDamageCondition;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityAction;
import io.github.apace100.origins.api.power.configuration.ConfiguredEntityCondition;
import io.github.apace100.origins.api.power.configuration.power.ICooldownPowerConfiguration;
import io.github.apace100.origins.util.HudRender;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ConditionedCombatActionConfiguration(int duration, HudRender hudRender,
												   @Nullable ConfiguredDamageCondition<?, ?> damageCondition,
												   @Nullable ConfiguredEntityCondition<?, ?> targetCondition,
												   ConfiguredEntityAction<?, ?> entityAction) implements ICooldownPowerConfiguration {
	public static final Codec<ConditionedCombatActionConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("cooldown").forGetter(ConditionedCombatActionConfiguration::duration),
			HudRender.CODEC.optionalFieldOf("hud_render", HudRender.DONT_RENDER).forGetter(ConditionedCombatActionConfiguration::hudRender),
			ConfiguredDamageCondition.CODEC.optionalFieldOf("damage_condition").forGetter(x -> Optional.ofNullable(x.damageCondition())),
			ConfiguredEntityCondition.CODEC.optionalFieldOf("target_condition").forGetter(x -> Optional.ofNullable(x.targetCondition())),
			ConfiguredEntityAction.CODEC.fieldOf("entity_action").forGetter(ConditionedCombatActionConfiguration::entityAction)
	).apply(instance, (t1, t2, t3, t4, t5) -> new ConditionedCombatActionConfiguration(t1, t2, t3.orElse(null), t4.orElse(null), t5)));

	public boolean check(LivingEntity target, DamageSource source, float amount) {
		return ConfiguredEntityCondition.check(this.targetCondition(), target) && ConfiguredDamageCondition.check(this.damageCondition(), source, amount);
	}
}
