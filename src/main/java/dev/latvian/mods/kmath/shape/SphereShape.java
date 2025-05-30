package dev.latvian.mods.kmath.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kmath.SpherePoints;
import dev.latvian.mods.kmath.texture.UV;
import dev.latvian.mods.kmath.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SphereShape(float radius) implements Shape {
	public static final MapCodec<SphereShape> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("radius").forGetter(SphereShape::radius)
	).apply(instance, SphereShape::new));

	public static final StreamCodec<ByteBuf, SphereShape> STREAM_CODEC = ByteBufCodecs.FLOAT.map(SphereShape::new, SphereShape::radius);

	@Override
	public ShapeType type() {
		return ShapeType.SPHERE;
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
		SpherePoints.M.buildLines(x, y, z, radius, callback);
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		SpherePoints.M.buildQuads(x, y, z, radius, UV.FULL, callback);
	}
}
