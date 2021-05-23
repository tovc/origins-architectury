package io.github.apace100.origins.power.action.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class ApplyEffectAction implements Consumer<Entity> {

	public static final Codec<ApplyEffectAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.STATUS_EFFECT_INSTANCE.optionalFieldOf("effect").forGetter(ApplyEffectAction::getSingular),
			OriginsCodecs.listOf(OriginsCodecs.STATUS_EFFECT_INSTANCE).optionalFieldOf("effects", ImmutableList.of()).forGetter(ApplyEffectAction::getMultiple)
	).apply(instance, ApplyEffectAction::new));

	private final Set<StatusEffectInstance> effects;

	public ApplyEffectAction(Optional<StatusEffectInstance> effect, List<StatusEffectInstance> effects) {
		ImmutableSet.Builder<StatusEffectInstance> builder = ImmutableSet.<StatusEffectInstance>builder().addAll(effects);
		effect.ifPresent(builder::add);
		this.effects = builder.build();
	}

	private Optional<StatusEffectInstance> getSingular() {
		return this.effects.size() == 1 ? this.effects.stream().findFirst() : Optional.empty();
	}

	private List<StatusEffectInstance> getMultiple() {
		return this.effects.size() == 1 ? ImmutableList.of() : ImmutableList.copyOf(this.effects);
	}

	@Override
	public void accept(Entity entity) {
		if (!(entity instanceof LivingEntity))
			return;
		for (StatusEffectInstance effect : effects)
			((LivingEntity) entity).applyStatusEffect(new StatusEffectInstance(effect));
	}
}
