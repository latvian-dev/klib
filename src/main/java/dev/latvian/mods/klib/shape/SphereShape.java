package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3fc;

public record SphereShape(float radius) implements Shape {
	public static final SphereShape UNIT = new SphereShape(0.5F);

	public static final MapCodec<SphereShape> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("radius").forGetter(SphereShape::radius)
	).apply(instance, SphereShape::new));

	public static final StreamCodec<ByteBuf, SphereShape> STREAM_CODEC = ByteBufCodecs.FLOAT.map(SphereShape::new, SphereShape::radius);
	public static final ShapeType TYPE = new ShapeType("sphere", CODEC, STREAM_CODEC);

	@Override
	public ShapeType type() {
		return TYPE;
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
		SpherePoints.M.buildLines(x, y, z, radius, callback);
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		SpherePoints.M.buildQuads(x, y, z, radius, callback);
	}

	@Override
	public boolean contains(Vector3fc p) {
		return p.lengthSquared() <= radius * radius;
	}
}
