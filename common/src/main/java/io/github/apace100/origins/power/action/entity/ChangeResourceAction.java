package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.power.CooldownPower;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.VariableIntPower;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Consumer;

public class ChangeResourceAction implements Consumer<Entity> {

	public static final Codec<ChangeResourceAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.POWER_TYPE.fieldOf("resource").forGetter(x -> x.resource),
			Codec.INT.fieldOf("change").forGetter(x -> x.amount)
	).apply(instance, ChangeResourceAction::new));

	private final PowerType<?> resource;
	private final int amount;

	public ChangeResourceAction(PowerType<?> resource, int amount) {
		this.resource = resource;
		this.amount = amount;
	}

	@Override
	public void accept(Entity entity) {
		if(entity instanceof PlayerEntity) {
			OriginComponent component = ModComponentsArchitectury.getOriginComponent(entity);
			Power p = component.getPower(resource);
			if(p instanceof VariableIntPower) {
				VariableIntPower vip = (VariableIntPower)p;
				int newValue = vip.getValue() + amount;
				vip.setValue(newValue);
				OriginComponent.sync((PlayerEntity)entity);
			} else if(p instanceof CooldownPower) {
				CooldownPower cp = (CooldownPower)p;
				cp.modify(amount);
				OriginComponent.sync((PlayerEntity)entity);
			}
		}
	}
}
