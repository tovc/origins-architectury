package io.github.apace100.origins.power.action.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class SpawnEffectCloudAction implements Consumer<Entity> {

	public static final Codec<SpawnEffectCloudAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.optionalFieldOf("radius", 3.0F).forGetter(x -> x.radius),
			Codec.FLOAT.optionalFieldOf("radius_on_use", 3.0F).forGetter(x -> x.radiusOnUse),
			Codec.INT.optionalFieldOf("radius_on_use", 10).forGetter(x -> x.waitTime),
			OriginsCodecs.STATUS_EFFECT_INSTANCE.optionalFieldOf("effect").forGetter(SpawnEffectCloudAction::getSingular),
			OriginsCodecs.listOf(OriginsCodecs.STATUS_EFFECT_INSTANCE).optionalFieldOf("effects", ImmutableList.of()).forGetter(SpawnEffectCloudAction::getMultiple)
	).apply(instance, SpawnEffectCloudAction::new));

	private final float radius;
	private final float radiusOnUse;
	private final int waitTime;
	private final Set<StatusEffectInstance> effects;

	public SpawnEffectCloudAction(float radius, float radiusOnUse, int waitTime, Optional<StatusEffectInstance> effect, List<StatusEffectInstance> effects) {
		this.radius = radius;
		this.radiusOnUse = radiusOnUse;
		this.waitTime = waitTime;
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
		AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(entity.world, entity.getX(), entity.getY(), entity.getZ());
		if (entity instanceof LivingEntity)
			areaEffectCloudEntity.setOwner((LivingEntity)entity);
		areaEffectCloudEntity.setRadius(this.radius);
		areaEffectCloudEntity.setRadiusOnUse(this.radiusOnUse);
		areaEffectCloudEntity.setWaitTime(this.waitTime);
		areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
		this.effects.stream().map(StatusEffectInstance::new).forEach(areaEffectCloudEntity::addEffect);

		entity.world.spawnEntity(areaEffectCloudEntity);
	}
}
