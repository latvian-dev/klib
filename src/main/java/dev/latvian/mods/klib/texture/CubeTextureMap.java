package dev.latvian.mods.klib.texture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CubeTextureMap(
	FaceTexture down,
	FaceTexture up,
	FaceTexture north,
	FaceTexture south,
	FaceTexture west,
	FaceTexture east
) {
	public static final CubeTextureMap EMPTY = new CubeTextureMap(FaceTexture.EMPTY);
	public static final CubeTextureMap WHITE = new CubeTextureMap(FaceTexture.WHITE);

	public static final Codec<CubeTextureMap> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		FaceTexture.CODEC.optionalFieldOf("down", FaceTexture.EMPTY).forGetter(CubeTextureMap::down),
		FaceTexture.CODEC.optionalFieldOf("up", FaceTexture.EMPTY).forGetter(CubeTextureMap::up),
		FaceTexture.CODEC.optionalFieldOf("north", FaceTexture.EMPTY).forGetter(CubeTextureMap::north),
		FaceTexture.CODEC.optionalFieldOf("south", FaceTexture.EMPTY).forGetter(CubeTextureMap::south),
		FaceTexture.CODEC.optionalFieldOf("west", FaceTexture.EMPTY).forGetter(CubeTextureMap::west),
		FaceTexture.CODEC.optionalFieldOf("east", FaceTexture.EMPTY).forGetter(CubeTextureMap::east)
	).apply(instance, CubeTextureMap::new));

	public static final Codec<CubeTextureMap> CODEC = KLibCodecs.or(DIRECT_CODEC, CubeTexturePatch.CODEC.xmap(CubeTexturePatch::toMap, CubeTextureMap::toPatch));

	public static final StreamCodec<ByteBuf, CubeTextureMap> STREAM_CODEC = CompositeStreamCodec.of(
		KLibStreamCodecs.optional(FaceTexture.STREAM_CODEC, FaceTexture.EMPTY), CubeTextureMap::down,
		KLibStreamCodecs.optional(FaceTexture.STREAM_CODEC, FaceTexture.EMPTY), CubeTextureMap::up,
		KLibStreamCodecs.optional(FaceTexture.STREAM_CODEC, FaceTexture.EMPTY), CubeTextureMap::north,
		KLibStreamCodecs.optional(FaceTexture.STREAM_CODEC, FaceTexture.EMPTY), CubeTextureMap::south,
		KLibStreamCodecs.optional(FaceTexture.STREAM_CODEC, FaceTexture.EMPTY), CubeTextureMap::west,
		KLibStreamCodecs.optional(FaceTexture.STREAM_CODEC, FaceTexture.EMPTY), CubeTextureMap::east,
		CubeTextureMap::new
	);

	public static final DataType<CubeTextureMap> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC);

	public CubeTextureMap(FaceTexture all) {
		this(all, all, all, all, all, all);
	}

	public CubeTextureMap merge(CubeTexturePatch with) {
		if (with == CubeTexturePatch.EMPTY) {
			return this;
		}

		return new CubeTextureMap(
			with.down().orElse(with.all().orElse(down)),
			with.up().orElse(with.all().orElse(up)),
			with.north().orElse(with.all().orElse(north)),
			with.south().orElse(with.all().orElse(south)),
			with.west().orElse(with.all().orElse(west)),
			with.east().orElse(with.all().orElse(east))
		);
	}

	public CubeTexturePatch toPatch() {
		if (this == EMPTY) {
			return CubeTexturePatch.EMPTY;
		} else if (down != FaceTexture.EMPTY && down == up && down == north && down == south && down == west && down == east) {
			return new CubeTexturePatch(
				Optional.of(down),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty()
			);
		} else {
			return CubeTexturePatch.of(
				Optional.empty(),
				down == FaceTexture.EMPTY ? Optional.empty() : Optional.of(down),
				up == FaceTexture.EMPTY ? Optional.empty() : Optional.of(up),
				north == FaceTexture.EMPTY ? Optional.empty() : Optional.of(north),
				south == FaceTexture.EMPTY ? Optional.empty() : Optional.of(south),
				west == FaceTexture.EMPTY ? Optional.empty() : Optional.of(west),
				east == FaceTexture.EMPTY ? Optional.empty() : Optional.of(east)
			);
		}
	}
}
