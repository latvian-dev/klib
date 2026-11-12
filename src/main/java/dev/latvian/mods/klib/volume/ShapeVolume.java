package dev.latvian.mods.klib.volume;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.math.AAFBB;
import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.math.HitBuilder;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.shape.Shape;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public record ShapeVolume(Vec3 pos, Ref<Shape> shape, AABB box) implements Volume {
	public static final DynamicType<ByteBuf, Volume> TYPE = DynamicType.create(
		"shape",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			MCCodecs.VEC3.fieldOf("pos").forGetter(ShapeVolume::pos),
			Shape.CODEC.fieldOf("shape").forGetter(ShapeVolume::shape)
		).apply(instance, ShapeVolume::new)),
		CompositeStreamCodec.of(
			MCStreamCodecs.VEC3, ShapeVolume::pos,
			Shape.STREAM_CODEC, ShapeVolume::shape,
			ShapeVolume::new
		)
	);

	public ShapeVolume(Vec3 pos, Ref<Shape> shape) {
		this(pos, shape, shape.value().box().aabb(pos));
	}

	@Override
	public DynamicType<ByteBuf, Volume> type() {
		return TYPE;
	}

	@Override
	public Vec3 centerPos() {
		return pos;
	}

	@Override
	public boolean hit(HitBuilder hit) {
		return shape.value().hit(pos, hit);
	}

	@Override
	public boolean contains(double x, double y, double z) {
		return shape.value().contains(
			(float) (x - pos.x),
			(float) (y - pos.y),
			(float) (z - pos.z)
		);
	}

	@Override
	public boolean intersects(AABB box) {
		return shape.value().intersects(AAFBB.relative(box, pos));
	}

	@Override
	public double closestDistanceTo(Vec3 to) {
		return shape.value().closestDistanceTo(
			(float) (to.x - pos.x),
			(float) (to.y - pos.y),
			(float) (to.z - pos.z)
		);
	}

	@Override
	public Volume move(double x, double y, double z) {
		return new ShapeVolume(pos.add(x, y, z), shape, box.move(x, y, z));
	}

	@Override
	public Volume scale(double x, double y, double z) {
		return new ShapeVolume(pos, shape.value().scale(x, y, z).ref());
	}

	@Override
	public boolean isVisible(FrustumCheck frustum) {
		return shape.value().isVisible(pos.x, pos.y, pos.z, frustum);
	}

	@Override
	public void buildLines(LineVertexCallback callback, Vec3 cameraPos) {
		shape.value().buildLines(new Vector3f(
			(float) (pos.x - cameraPos.x),
			(float) (pos.y - cameraPos.y),
			(float) (pos.z - cameraPos.z)
		), callback);
	}

	@Override
	public void buildOuterLines(LineVertexCallback callback, Vec3 cameraPos) {
		shape.value().buildOuterLines(new Vector3f(
			(float) (pos.x - cameraPos.x),
			(float) (pos.y - cameraPos.y),
			(float) (pos.z - cameraPos.z)
		), callback);
	}

	@Override
	public void buildQuads(QuadVertexCallback callback, Vec3 cameraPos) {
		shape.value().buildQuads(new Vector3f(
			(float) (pos.x - cameraPos.x),
			(float) (pos.y - cameraPos.y),
			(float) (pos.z - cameraPos.z)
		), callback);
	}
}
