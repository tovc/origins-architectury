package io.github.apace100.origins.power.action.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import java.util.Optional;
import java.util.function.Consumer;

public class PlaySoundAction implements Consumer<Entity> {

	public static final Codec<PlaySoundAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.OPTIONAL_SOUND_EVENT.fieldOf("sound").forGetter(x -> x.sound),
			Codec.FLOAT.optionalFieldOf("volume", 1F).forGetter(x -> x.volume),
			Codec.FLOAT.optionalFieldOf("pitch", 1F).forGetter(x -> x.pitch)
	).apply(instance, PlaySoundAction::new));

	private final Optional<SoundEvent> sound;
	private final float volume;
	private final float pitch;

	public PlaySoundAction(Optional<SoundEvent> sound, float volume, float pitch) {
		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;
	}

	@Override
	public void accept(Entity entity) {
		if(entity instanceof PlayerEntity)
			sound.ifPresent(x -> entity.world.playSound(null, (entity).getX(), (entity).getY(), (entity).getZ(), x, SoundCategory.PLAYERS, volume, pitch));
	}
}
