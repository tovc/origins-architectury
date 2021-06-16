package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.power.CooldownPower;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Consumer;

public class TriggerCooldownAction implements Consumer<Entity> {
	public static final Codec<TriggerCooldownAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.POWER_TYPE.fieldOf("power").forGetter(x -> x.power)
	).apply(instance, TriggerCooldownAction::new));

	private final PowerType<?> power;

	public TriggerCooldownAction(PowerType<?> power) {this.power = power;}

	@Override
	public void accept(Entity entity) {
		if(entity instanceof PlayerEntity) {
			OriginComponent component = ModComponentsArchitectury.getOriginComponent(entity);
			Power p = component.getPower(power);
			if(p instanceof CooldownPower) {
				CooldownPower cp = (CooldownPower)p;
				cp.use();
			}
		}
	}
}
