package dev.latvian.mods.klib.volume;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.JOMLCodecs;
import dev.latvian.mods.klib.codec.JOMLStreamCodecs;
import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.math.HitBuilder;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.math.Rotation;
import dev.latvian.mods.klib.registry.DynamicType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;

public record RotatedBoxVolume(Vec3 pos, Vector3fc size, Rotation rotation, Matrix4fc matrix, Matrix4fc imatrix, AABB box, List<AABB> clipBox) implements Volume {
	public static RotatedBoxVolume of(Vec3 pos, Vector3fc size, Rotation rotation) {
		var matrix = new Matrix4f();
		rotation.rotateZXY(matrix);
		var imatrix = new Matrix4f(matrix).invert();

		var hsx = size.x() / 2F;
		var hsy = size.y() / 2F;
		var hsz = size.z() / 2F;

		var cnnn = new Vector3f(-hsx, -hsy, -hsz).mulPosition(matrix);
		var cpnn = new Vector3f(+hsx, -hsy, -hsz).mulPosition(matrix);
		var cnpn = new Vector3f(-hsx, +hsy, -hsz).mulPosition(matrix);
		var cppn = new Vector3f(+hsx, +hsy, -hsz).mulPosition(matrix);
		var cnnp = new Vector3f(-hsx, -hsy, +hsz).mulPosition(matrix);
		var cpnp = new Vector3f(+hsx, -hsy, +hsz).mulPosition(matrix);
		var cnpp = new Vector3f(-hsx, +hsy, +hsz).mulPosition(matrix);
		var cppp = new Vector3f(+hsx, +hsy, +hsz).mulPosition(matrix);

		var box = new AABB(
			pos.x() + KMath.min(cnnn.x, cpnn.x, cnpn.x, cppn.x, cnnp.x, cpnp.x, cnpp.x, cppp.x),
			pos.y() + KMath.min(cnnn.y, cpnn.y, cnpn.y, cppn.y, cnnp.y, cpnp.y, cnpp.y, cppp.y),
			pos.z() + KMath.min(cnnn.z, cpnn.z, cnpn.z, cppn.z, cnnp.z, cpnp.z, cnpp.z, cppp.z),
			pos.x() + KMath.max(cnnn.x, cpnn.x, cnpn.x, cppn.x, cnnp.x, cpnp.x, cnpp.x, cppp.x),
			pos.y() + KMath.max(cnnn.y, cpnn.y, cnpn.y, cppn.y, cnnp.y, cpnp.y, cnpp.y, cppp.y),
			pos.z() + KMath.max(cnnn.z, cpnn.z, cnpn.z, cppn.z, cnnp.z, cpnp.z, cnpp.z, cppp.z)
		);

		return new RotatedBoxVolume(pos, size, rotation, matrix, imatrix, box, List.of(new AABB(-hsx, -hsy, -hsz, hsx, hsy, hsz)));
	}

	public static final DynamicType<ByteBuf, Volume> TYPE = DynamicType.create(
		"rotated_box",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			MCCodecs.VEC3.fieldOf("pos").forGetter(RotatedBoxVolume::pos),
			JOMLCodecs.VEC3SC.fieldOf("size").forGetter(RotatedBoxVolume::size),
			Rotation.CODEC.fieldOf("rotation").forGetter(RotatedBoxVolume::rotation)
		).apply(instance, RotatedBoxVolume::of)),
		CompositeStreamCodec.of(
			MCStreamCodecs.VEC3, RotatedBoxVolume::pos,
			JOMLStreamCodecs.VEC3SC, RotatedBoxVolume::size,
			Rotation.STREAM_CODEC, RotatedBoxVolume::rotation,
			RotatedBoxVolume::of
		)
	);

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
		if (contains(hit.from)) {
			return false;
		}

		var rstart = new Vector3f((float) (hit.from.x - pos.x), (float) (hit.from.y - pos.y), (float) (hit.from.z - pos.z)).mulPosition(matrix);
		var rend = new Vector3f((float) (hit.to.x - pos.x), (float) (hit.to.y - pos.y), (float) (hit.to.z - pos.z)).mulPosition(matrix);

		var result = AABB.clip(clipBox, new Vec3(rstart.x, rstart.y, rstart.z), new Vec3(rend.x, rend.y, rend.z), BlockPos.ZERO);

		if (result != null && result.getType() == HitResult.Type.BLOCK) {
			var l = result.getLocation();
			var vec = new Vector3f((float) l.x, (float) l.y, (float) l.z).mulPosition(imatrix);
			var apos = new Vec3(vec.x + pos.x, vec.y + pos.y, vec.z + pos.z);
			hit.offer(apos, result.getDirection());
			return true;
		}

		return false;
	}

	@Override
	public boolean contains(double x, double y, double z) {
		if (box.contains(x, y, z)) {
			var vec = new Vector3f((float) (x - pos.x), (float) (y - pos.y), (float) (z - pos.z)).mulPosition(matrix);
			var hsx = size.x() / 2F;
			var hsy = size.y() / 2F;
			var hsz = size.z() / 2F;
			return vec.x >= -hsx && vec.x <= hsx && vec.y >= -hsy && vec.y <= hsy && vec.z >= -hsz && vec.z <= hsz;
		}

		return false;
	}

	@Override
	public boolean intersects(AABB box) {
		double cx = Math.clamp(pos.x, box.minX, box.maxX);
		double cy = Math.clamp(pos.y, box.minY, box.maxY);
		double cz = Math.clamp(pos.z, box.minZ, box.maxZ);

		var hsx = size.x() / 2F;
		var hsy = size.y() / 2F;
		var hsz = size.z() / 2F;

		var vec = new Vector3f((float) (cx - pos.x), (float) (cy - pos.y), (float) (cz - pos.z)).mulPosition(matrix);
		return vec.x >= -hsx && vec.x <= hsx && vec.y >= -hsy && vec.y <= hsy && vec.z >= -hsz && vec.z <= hsz;
	}

	@Override
	public Volume move(double x, double y, double z) {
		return new RotatedBoxVolume(
			pos.add(x, y, z),
			size,
			rotation,
			new Matrix4f(matrix),
			new Matrix4f(imatrix),
			box.move(x, y, z),
			clipBox.isEmpty() ? List.of() : clipBox.size() == 1 ? List.of(clipBox.getFirst().move(x, y, z)) : clipBox.stream().map(b -> b.move(x, y, z)).toList()
		);
	}

	@Override
	public Volume scale(double x, double y, double z) {
		return of(pos, size.mul((float) x, (float) y, (float) z, new Vector3f()), rotation);
	}
}
