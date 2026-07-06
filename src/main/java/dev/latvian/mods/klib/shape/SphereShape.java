package dev.latvian.mods.klib.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.VertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Mth;
import org.joml.Vector3fc;

public record SphereShape(float size) implements Shape {
	public static final UnitType<ByteBuf, Shape> UNIT_SPHERE = UnitType.create("unit_sphere", new SphereShape(1F));

	public static final DynamicType<ByteBuf, Shape> TYPE = DynamicType.create(
		"sphere",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.FLOAT.fieldOf("size").forGetter(SphereShape::size)
		).apply(instance, SphereShape::new)),
		CompositeStreamCodec.of(
			ByteBufCodecs.FLOAT, SphereShape::size,
			SphereShape::new
		)
	);

	@Override
	public CustomRegistryType<ByteBuf, Shape> type() {
		return TYPE;
	}

	@Override
	public void buildLines(float x, float y, float z, VertexCallback callback) {
		SpherePoints.M.buildLines(x, y, z, size, callback);
	}

	@Override
	public void buildQuads(float x, float y, float z, VertexCallback callback) {
		SpherePoints.M.buildQuads(x, y, z, size, callback);
	}

	@Override
	public boolean contains(Vector3fc p) {
		return p.lengthSquared() <= Mth.square(size / 2F);
	}

	@Override
	public Shape optimize() {
		return size == 1F ? UNIT_SPHERE.value() : this;
	}
}
