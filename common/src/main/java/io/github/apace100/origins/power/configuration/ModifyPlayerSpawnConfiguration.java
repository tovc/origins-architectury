package io.github.apace100.origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.api.IOriginsFeatureConfiguration;
import io.github.apace100.origins.util.OriginsCodecs;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Pair;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ModifyPlayerSpawnConfiguration(RegistryKey<World> dimension, float distanceMultiplier,
											 @Nullable RegistryKey<Biome> biome, String strategy,
											 @Nullable StructureFeature<?> structure,
											 @Nullable SoundEvent sound) implements IOriginsFeatureConfiguration {
	public static final Codec<ModifyPlayerSpawnConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			OriginsCodecs.DIMENSION.fieldOf("dimension").forGetter(ModifyPlayerSpawnConfiguration::dimension),
			Codec.FLOAT.optionalFieldOf("dimension_distance_multiplier", 0F).forGetter(ModifyPlayerSpawnConfiguration::distanceMultiplier),
			OriginsCodecs.BIOME.optionalFieldOf("biome").forGetter(x -> Optional.ofNullable(x.biome())),
			Codec.STRING.optionalFieldOf("spawn_strategy", "default").forGetter(ModifyPlayerSpawnConfiguration::strategy),
			OriginsCodecs.OPTIONAL_STRUCTURE_FEATURE.optionalFieldOf("structure", Optional.empty()).forGetter(x -> Optional.ofNullable(x.structure())),
			OriginsCodecs.OPTIONAL_SOUND_EVENT.optionalFieldOf("respawn_sound", Optional.empty()).forGetter(x -> Optional.ofNullable(x.sound()))
	).apply(instance, (t1, t2, t3, t4, t5, t6) -> new ModifyPlayerSpawnConfiguration(t1, t2, t3.orElse(null), t4, t5.orElse(null), t6.orElse(null))));

	private static BlockPos getStructureLocation(PlayerEntity player, StructureFeature<?> structure, RegistryKey<World> dimension) {
		BlockPos blockPos = new BlockPos(0, 70, 0);
		ServerWorld serverWorld = player.getServer().getWorld(dimension);
		BlockPos blockPos2 = serverWorld.locateStructure(structure, blockPos, 100, false);
		//FrostburnOrigins.LOGGER.warn("Unrecognized dimension id '" + dimensionId + "', defaulting to id '0', OVERWORLD");
		if (blockPos2 == null) {
			Origins.LOGGER.warn("Could not find '" + structure.getName() + "' in dimension: " + dimension.getValue());
			return null;
		} else {
			return blockPos2;
		}
	}

	private static Vec3d getValidSpawn(BlockPos startPos, int range, ServerWorld world) {
		//Force load the chunk in which we are working.
		//This method will generate the chunk if it needs to.
		world.getChunk(startPos.getX() >> 4, startPos.getZ() >> 4, ChunkStatus.FULL, true);
		// (di, dj) is a vector - direction in which we move right now
		int dx = 1;
		int dz = 0;
		// length of current segment
		int segmentLength = 1;
		BlockPos.Mutable mutable = startPos.mutableCopy();
		// center of our starting structure, or dimension
		int center = startPos.getY();
		// Our valid spawn location
		Vec3d tpPos;

		// current position (x, z) and how much of current segment we passed
		int x = startPos.getX();
		int z = startPos.getZ();
		//position to check up, or down
		int segmentPassed = 0;
		// increase y check
		int i = 0;
		// Decrease y check
		int d = 0;
		while (i < world.getDimensionHeight() || d > 0) {
			for (int coordinateCount = 0; coordinateCount < range; ++coordinateCount) {
				// make a step, add 'direction' vector (di, dj) to current position (i, j)
				x += dx;
				z += dz;
				++segmentPassed;
				mutable.setX(x);
				mutable.setZ(z);
				mutable.setY(center + i);
				tpPos = Dismounting.method_30769(EntityType.PLAYER, world, mutable, true);
				//Avoid spawning on the nether roof.
				if (tpPos != null && tpPos.getY() < world.getDimensionHeight()) {
					return (tpPos);
				} else {
					mutable.setY(center + d);
					tpPos = Dismounting.method_30769(EntityType.PLAYER, world, mutable, true);
					if (tpPos != null && tpPos.getY() < world.getDimensionHeight()) {
						return (tpPos);
					}
				}

				if (segmentPassed == segmentLength) {
					// done with current segment
					segmentPassed = 0;

					// 'rotate' directions
					int buffer = dx;
					dx = -dz;
					dz = buffer;

					// increase segment length if necessary
					if (dz == 0) {
						++segmentLength;
					}
				}
			}
			i++;
			d--;
		}
		return (null);
	}

	public Pair<ServerWorld, BlockPos> getSpawn(PlayerEntity player, boolean isSpawnObstructed) {
		if (player instanceof ServerPlayerEntity serverPlayer) {
			ServerWorld world = serverPlayer.getServerWorld().getServer().getWorld(dimension);
			BlockPos regularSpawn = serverPlayer.getServerWorld().getServer().getWorld(World.OVERWORLD).getSpawnPos();
			BlockPos spawnToDimPos;
			//int iterations = (world.getDimensionHeight() / 2) - 8;
			int center = world.getDimensionHeight() / 2;
			BlockPos.Mutable mutable;
			Vec3d tpPos;
			int range = 64;

			switch (this.strategy()) {
				case "center":
					spawnToDimPos = new BlockPos(0, center, 0);
					break;

				case "default":
					if (this.distanceMultiplier() != 0) {
						spawnToDimPos = new BlockPos(regularSpawn.getX() * this.distanceMultiplier(), regularSpawn.getY(), regularSpawn.getZ() * this.distanceMultiplier());
					} else {
						spawnToDimPos = new BlockPos(regularSpawn.getX(), regularSpawn.getY(), regularSpawn.getZ());
					}
					break;

				default:
					Origins.LOGGER.warn("This case does nothing. The game crashes if there is no spawn strategy set");
					if (this.distanceMultiplier() != 0) {
						spawnToDimPos = new BlockPos(regularSpawn.getX() * this.distanceMultiplier(), regularSpawn.getY(), regularSpawn.getZ() * this.distanceMultiplier());
					} else {
						spawnToDimPos = new BlockPos(regularSpawn.getX(), regularSpawn.getY(), regularSpawn.getZ());
					}
			}

			if (this.biome() != null) {
				Optional<Biome> biomeOptional = world.getRegistryManager().get(Registry.BIOME_KEY).getOrEmpty(this.biome());
				if (biomeOptional.isPresent()) {
					BlockPos biomePos = world.locateBiome(biomeOptional.get(), spawnToDimPos, 6400, 8);
					if (biomePos != null)
						spawnToDimPos = biomePos;
					else
						Origins.LOGGER.warn("Could not find biome \"{}\" in dimension \"{}\".", this.biome(), this.dimension());
				} else
					Origins.LOGGER.warn("Biome with ID \"{}\" was not registered.", this.biome());
			}

			if (structure == null) {
				tpPos = getValidSpawn(spawnToDimPos, range, world);
			} else {
				BlockPos structurePos = getStructureLocation(player, structure, dimension);
				ChunkPos structureChunkPos;

				if (structurePos == null) {
					return null;
				}
				structureChunkPos = new ChunkPos(structurePos.getX() >> 4, structurePos.getZ() >> 4);
				StructureStart<?> structureStart = world.getStructureAccessor().getStructureStart(ChunkSectionPos.from(structureChunkPos, 0), structure, world.getChunk(structurePos));
				BlockPos structureCenter = new BlockPos(structureStart.getBoundingBox().getCenter());
				tpPos = getValidSpawn(structureCenter, range, world);
			}

			if (tpPos != null) {
				mutable = new BlockPos(tpPos.x, tpPos.y, tpPos.z).mutableCopy();
				BlockPos spawnLocation = mutable;
				world.getChunkManager().addTicket(ChunkTicketType.START, new ChunkPos(spawnLocation), 11, Unit.INSTANCE);
				return new Pair<>(world, spawnLocation);
			}
			return null;
		}
		return null;
	}
}
