package dev.latvian.mods.klib.math;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.JOMLStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;

public record Line3f(Vector3fc start, Vector3fc end) {
	public static final Codec<Line3f> CODEC = Codec.FLOAT.listOf(6, 6).xmap(Line3f::new, Line3f::toFloatList);

	public static final StreamCodec<ByteBuf, Line3f> STREAM_CODEC = CompositeStreamCodec.of(
		JOMLStreamCodecs.VEC3C, Line3f::start,
		JOMLStreamCodecs.VEC3C, Line3f::end,
		Line3f::new
	);

	public static Line3f of(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return new Line3f(Vec3f.of(minX, minY, minZ), Vec3f.of(maxX, maxY, maxZ));
	}

	public Line3f(List<Float> l) {
		this(Vec3f.of(l.get(0), l.get(1), l.get(2)), Vec3f.of(l.get(3), l.get(4), l.get(5)));
	}

	public float dx() {
		return end.x() - start.x();
	}

	public float dy() {
		return end.y() - start.y();
	}

	public float dz() {
		return end.z() - start.z();
	}

	public Vector3f delta() {
		return new Vector3f(dx(), dy(), dz());
	}

	public List<Float> toFloatList() {
		return List.of(start.x(), start.y(), start.z(), end.x(), end.y(), end.z());
	}
}
