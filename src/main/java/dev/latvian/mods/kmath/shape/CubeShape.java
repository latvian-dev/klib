package dev.latvian.mods.kmath.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kmath.render.BoxBuilder;
import dev.latvian.mods.kmath.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CubeShape(float radius) implements Shape {
	public static final MapCodec<CubeShape> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("radius").forGetter(CubeShape::radius)
	).apply(instance, CubeShape::new));

	public static final StreamCodec<ByteBuf, CubeShape> STREAM_CODEC = ByteBufCodecs.FLOAT.map(CubeShape::new, CubeShape::radius);

	@Override
	public ShapeType type() {
		return ShapeType.CUBE;
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
		BoxBuilder.lines(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius, callback);
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		BoxBuilder.quads(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius, callback);
	}
}
