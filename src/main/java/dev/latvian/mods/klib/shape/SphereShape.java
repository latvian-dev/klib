package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.util.ID;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import org.joml.Vector3fc;

public record SphereShape(float radius) implements Shape {
	public static final SphereShape UNIT_SPHERE = new SphereShape(0.5F);

	public static final CustomRegistryType<ByteBuf, Shape> TYPE = Shape.REGISTRY.dynamic(ID.klib("sphere"),
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.FLOAT.fieldOf("radius").forGetter(SphereShape::radius)
		).apply(instance, SphereShape::new)),
		CompositeStreamCodec.of(
			ByteBufCodecs.FLOAT, SphereShape::radius,
			SphereShape::new
		)
	);

	@Override
	public CustomRegistryType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
		SpherePoints.M.buildLines(x, y, z, radius * 2F, callback);
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		SpherePoints.M.buildQuads(x, y, z, radius * 2F, callback);
	}

	@Override
	public boolean contains(Vector3fc p) {
		return p.lengthSquared() <= radius * radius;
	}
}
