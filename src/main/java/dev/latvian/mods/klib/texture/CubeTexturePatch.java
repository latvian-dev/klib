package dev.latvian.mods.klib.texture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record CubeTexturePatch(
	Optional<FaceTexture> all,
	Optional<FaceTexture> down,
	Optional<FaceTexture> up,
	Optional<FaceTexture> north,
	Optional<FaceTexture> south,
	Optional<FaceTexture> west,
	Optional<FaceTexture> east
) {
	public static final CubeTexturePatch EMPTY = new CubeTexturePatch(
		Optional.empty(),
		Optional.empty(),
		Optional.empty(),
		Optional.empty(),
		Optional.empty(),
		Optional.empty(),
		Optional.empty()
	);

	public static CubeTexturePatch of(
		Optional<FaceTexture> all,
		Optional<FaceTexture> down,
		Optional<FaceTexture> up,
		Optional<FaceTexture> north,
		Optional<FaceTexture> south,
		Optional<FaceTexture> west,
		Optional<FaceTexture> east
	) {
		if (all.isEmpty() && down.isEmpty() && up.isEmpty() && north.isEmpty() && south.isEmpty() && west.isEmpty() && east.isEmpty()) {
			return EMPTY;
		} else {
			return new CubeTexturePatch(all, down, up, north, south, west, east);
		}
	}

	public static final Codec<CubeTexturePatch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		FaceTexture.CODEC.optionalFieldOf("all").forGetter(CubeTexturePatch::all),
		FaceTexture.CODEC.optionalFieldOf("down").forGetter(CubeTexturePatch::down),
		FaceTexture.CODEC.optionalFieldOf("up").forGetter(CubeTexturePatch::up),
		FaceTexture.CODEC.optionalFieldOf("north").forGetter(CubeTexturePatch::north),
		FaceTexture.CODEC.optionalFieldOf("south").forGetter(CubeTexturePatch::south),
		FaceTexture.CODEC.optionalFieldOf("west").forGetter(CubeTexturePatch::west),
		FaceTexture.CODEC.optionalFieldOf("east").forGetter(CubeTexturePatch::east)
	).apply(instance, CubeTexturePatch::of));

	public static final StreamCodec<ByteBuf, CubeTexturePatch> STREAM_CODEC = CompositeStreamCodec.of(
		FaceTexture.OPTIONAL_STREAM_CODEC, CubeTexturePatch::all,
		FaceTexture.OPTIONAL_STREAM_CODEC, CubeTexturePatch::down,
		FaceTexture.OPTIONAL_STREAM_CODEC, CubeTexturePatch::up,
		FaceTexture.OPTIONAL_STREAM_CODEC, CubeTexturePatch::north,
		FaceTexture.OPTIONAL_STREAM_CODEC, CubeTexturePatch::south,
		FaceTexture.OPTIONAL_STREAM_CODEC, CubeTexturePatch::west,
		FaceTexture.OPTIONAL_STREAM_CODEC, CubeTexturePatch::east,
		CubeTexturePatch::of
	);

	public static final DataType<CubeTexturePatch> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC);

	public CubeTexturePatch(FaceTexture all) {
		this(Optional.of(all), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
	}

	public CubeTextureMap toMap() {
		return CubeTextureMap.EMPTY.merge(this);
	}
}
