package dev.latvian.mods.klib.math;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public record LevelVolume(ResourceKey<Level> dimension, AAIBB volume) {
	public static final Codec<LevelVolume> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		MCCodecs.DIMENSION.optionalFieldOf("dimension", Level.OVERWORLD).forGetter(LevelVolume::dimension),
		AAIBB.CODEC.fieldOf("volume").forGetter(LevelVolume::volume)
	).apply(instance, LevelVolume::new));

	public static final Codec<LevelVolume> CODEC = Codec.either(DIRECT_CODEC, AAIBB.CODEC).xmap(e -> e.map(Function.identity(), v -> new LevelVolume(Level.OVERWORLD, v)), v -> v.dimension == Level.OVERWORLD ? Either.right(v.volume) : Either.left(v));

	public static final StreamCodec<RegistryFriendlyByteBuf, LevelVolume> STREAM_CODEC = CompositeStreamCodec.of(
		KLibStreamCodecs.optional(MCStreamCodecs.DIMENSION, Level.OVERWORLD), LevelVolume::dimension,
		AAIBB.STREAM_CODEC, LevelVolume::volume,
		LevelVolume::new
	);

	public static final DataType<LevelVolume> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC);

	public LevelVolume(ResourceKey<Level> dimension, Vec3i start, Vec3i end) {
		this(dimension, new AAIBB(start, end));
	}

	public LevelVolume(ResourceKey<Level> dimension, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		this(dimension, new AAIBB(minX, minY, minZ, maxX, maxY, maxZ));
	}
}
