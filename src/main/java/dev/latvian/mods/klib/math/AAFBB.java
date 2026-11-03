package dev.latvian.mods.klib.math;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;

public record AAFBB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
	public static final AAFBB EMPTY = new AAFBB(0F, 0F, 0F, 0F, 0F, 0F);
	public static final AAFBB FULL = new AAFBB(0F, 0F, 0F, 1F, 1F, 1F);
	public static final AAFBB FULL_16 = new AAFBB(0F, 0F, 0F, 16F, 16F, 16F);
	public static final AAFBB CENTERED = new AAFBB(-0.5F, -0.5F, -0.5F, 0.5F, 0.5F, 0.5F);
	public static final AAFBB CENTERED_X_PLANE = new AAFBB(0F, -0.5F, -0.5F, 0F, 0.5F, 0.5F);
	public static final AAFBB CENTERED_Y_PLANE = new AAFBB(-0.5F, 0F, -0.5F, 0.5F, 0F, 0.5F);
	public static final AAFBB CENTERED_Z_PLANE = new AAFBB(-0.5F, -0.5F, 0F, 0.5F, 0.5F, 0F);
	public static final AAFBB CENTERED_X_AXIS = new AAFBB(-0.5F, 0F, 0F, 0.5F, 0F, 0F);
	public static final AAFBB CENTERED_Y_AXIS = new AAFBB(0F, -0.5F, 0F, 0F, 0.5F, 0F);
	public static final AAFBB CENTERED_Z_AXIS = new AAFBB(0F, 0F, -0.5F, 0F, 0F, 0.5F);
	public static final AAFBB INFINITE = new AAFBB(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

	public static final Codec<AAFBB> CODEC = Codec.FLOAT.listOf(6, 6).xmap(AAFBB::new, AAFBB::toFloatList);

	public static final StreamCodec<ByteBuf, AAFBB> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public AAFBB decode(ByteBuf buf) {
			int flags = buf.readByte() & 0xFF;

			if (flags == 64) {
				return EMPTY;
			} else if (flags == 128) {
				return INFINITE;
			} else if (flags == 0b111111) {
				return FULL;
			}

			float minX = ((flags & 1) != 0) ? 0F : buf.readFloat();
			float minY = ((flags & 2) != 0) ? 0F : buf.readFloat();
			float minZ = ((flags & 4) != 0) ? 0F : buf.readFloat();
			float maxX = ((flags & 8) != 0) ? 1F : buf.readFloat();
			float maxY = ((flags & 16) != 0) ? 1F : buf.readFloat();
			float maxZ = ((flags & 32) != 0) ? 1F : buf.readFloat();
			return new AAFBB(minX, minY, minZ, maxX, maxY, maxZ);
		}

		@Override
		public void encode(ByteBuf buf, AAFBB value) {
			if (value.isEmpty()) {
				buf.writeByte(64);
				return;
			} else if (value.isInfinite()) {
				buf.writeByte(128);
				return;
			} else if (value.isFull()) {
				buf.writeByte(0b111111);
				return;
			}

			int flags = 0;
			flags |= value.minX == 0F ? 1 : 0;
			flags |= value.minY == 0F ? 2 : 0;
			flags |= value.minZ == 0F ? 4 : 0;
			flags |= value.maxX == 1F ? 8 : 0;
			flags |= value.maxY == 1F ? 16 : 0;
			flags |= value.maxZ == 1F ? 32 : 0;
			buf.writeByte(flags);

			if (value.minX != 0F) {
				buf.writeFloat(value.minX);
			}

			if (value.minY != 0F) {
				buf.writeFloat(value.minY);
			}

			if (value.minZ != 0F) {
				buf.writeFloat(value.minZ);
			}

			if (value.maxX != 1F) {
				buf.writeFloat(value.maxX);
			}

			if (value.maxY != 1F) {
				buf.writeFloat(value.maxY);
			}

			if (value.maxZ != 1F) {
				buf.writeFloat(value.maxZ);
			}
		}
	};

	public static AAFBB of(AABB box) {
		return new AAFBB((float) box.minX, (float) box.minY, (float) box.minZ, (float) box.maxX, (float) box.maxY, (float) box.maxZ);
	}

	public static AAFBB centered(float sizeX, float sizeY, float sizeZ) {
		return new AAFBB(-sizeX / 2F, -sizeY / 2F, -sizeZ / 2F, sizeX / 2F, sizeY / 2F, sizeZ / 2F);
	}

	public static AAFBB centered(float size) {
		return centered(size, size, size);
	}

	public static AAFBB between(Vector3fc p1, Vector3fc p2) {
		return new AAFBB(p1.x(), p1.y(), p1.z(), p2.x(), p2.y(), p2.z());
	}

	public static AAFBB max(List<AAFBB> boxes) {
		if (boxes.isEmpty()) {
			return EMPTY;
		} else if (boxes.size() == 1) {
			return boxes.getFirst();
		} else {
			var first = boxes.getFirst();
			var minX = first.minX;
			var minY = first.minY;
			var minZ = first.minZ;
			var maxX = first.maxX;
			var maxY = first.maxY;
			var maxZ = first.maxZ;

			for (int i = 1; i < boxes.size(); i++) {
				var box = boxes.get(i);
				minX = Math.min(minX, box.minX);
				minY = Math.min(minY, box.minY);
				minZ = Math.min(minZ, box.minZ);
				maxX = Math.max(maxX, box.maxX);
				maxY = Math.max(maxY, box.maxY);
				maxZ = Math.max(maxZ, box.maxZ);
			}

			return new AAFBB(minX, minY, minZ, maxX, maxY, maxZ);
		}
	}

	public AAFBB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.minX = Math.min(minX, maxX);
		this.minY = Math.min(minY, maxY);
		this.minZ = Math.min(minZ, maxZ);
		this.maxX = Math.max(minX, maxX);
		this.maxY = Math.max(minY, maxY);
		this.maxZ = Math.max(minZ, maxZ);
	}

	public AAFBB(List<Float> list) {
		this(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4), list.get(5));
	}

	public Vector3f min() {
		return new Vector3f(minX, minY, minZ);
	}

	public Vector3f max() {
		return new Vector3f(maxX, maxY, maxZ);
	}

	public float sizeX() {
		return maxX - minX;
	}

	public float sizeY() {
		return maxY - minY;
	}

	public float sizeZ() {
		return maxZ - minZ;
	}

	public AABB aabb() {
		return new AABB(minX, minY, minZ, maxX + 1D, maxY + 1D, maxZ + 1D);
	}

	public float[] toFloatArray() {
		return new float[]{minX, minY, minZ, maxX, maxY, maxZ};
	}

	public List<Float> toFloatList() {
		return List.of(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public boolean intersects(AAFBB aabb) {
		return intersects(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
	}

	public boolean intersects(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ;
	}

	public boolean intersects(Vector3fc min, Vector3fc max) {
		return intersects(
			Math.min(min.x(), max.x()),
			Math.min(min.y(), max.y()),
			Math.min(min.z(), max.z()),
			Math.max(min.x(), max.x()),
			Math.max(min.y(), max.y()),
			Math.max(min.z(), max.z())
		);
	}

	public boolean contains(Vector3fc vec) {
		return contains(vec.x(), vec.y(), vec.z());
	}

	public boolean contains(float x, float y, float z) {
		return x >= minX && x < maxX && y >= minY && y < maxY && z >= minZ && z < maxZ;
	}

	public boolean isEmpty() {
		return this == EMPTY || minX == 0F && minY == 0F && minZ == 0F && maxX == 0F && maxY == 0F && maxZ == 0F;
	}

	public boolean isFull() {
		return this == FULL || minX == 0F && minY == 0F && minZ == 0F && maxX == 1F && maxY == 1F && maxZ == 1F;
	}

	public boolean isInfinite() {
		return this == INFINITE || Float.isInfinite(minX) && Float.isInfinite(minY) && Float.isInfinite(minZ) && Float.isInfinite(maxX) && Float.isInfinite(maxY);
	}

	public float clip(float x0, float y0, float z0, float x1, float y1, float z1) {
		float dirX = x1 - x0, dirY = y1 - y0, dirZ = z1 - z0;
		float invDirX = 1F / dirX, invDirY = 1F / dirY, invDirZ = 1F / dirZ;
		float tNear, tFar, tymin, tymax, tzmin, tzmax;

		if (invDirX >= 0F) {
			tNear = (minX - x0) * invDirX;
			tFar = (maxX - x0) * invDirX;
		} else {
			tNear = (maxX - x0) * invDirX;
			tFar = (minX - x0) * invDirX;
		}

		if (invDirY >= 0F) {
			tymin = (minY - y0) * invDirY;
			tymax = (maxY - y0) * invDirY;
		} else {
			tymin = (maxY - y0) * invDirY;
			tymax = (minY - y0) * invDirY;
		}

		if (tNear > tymax || tymin > tFar) {
			return Float.NaN;
		}

		if (invDirZ >= 0F) {
			tzmin = (minZ - z0) * invDirZ;
			tzmax = (maxZ - z0) * invDirZ;
		} else {
			tzmin = (maxZ - z0) * invDirZ;
			tzmax = (minZ - z0) * invDirZ;
		}

		if (tNear > tzmax || tzmin > tFar) {
			return Float.NaN;
		}

		tNear = tymin > tNear || Float.isNaN(tNear) ? tymin : tNear;
		tFar = tymax < tFar || Float.isNaN(tFar) ? tymax : tFar;
		tNear = Math.max(tzmin, tNear);
		tFar = Math.min(tzmax, tFar);

		if (tNear <= tFar && tNear <= 1F && tFar >= 0F) {
			if (tNear < 0F && tFar <= 1F) {
				tNear = tFar;
			}

			return tNear;
		}

		return Float.NaN;
	}

	public Vector3f nnn() {
		return new Vector3f(minX, minY, minZ);
	}

	public Vector3f pnn() {
		return new Vector3f(maxX, minY, minZ);
	}

	public Vector3f pnp() {
		return new Vector3f(maxX, minY, maxZ);
	}

	public Vector3f nnp() {
		return new Vector3f(minX, minY, maxZ);
	}

	public Vector3f npn() {
		return new Vector3f(minX, maxY, minZ);
	}

	public Vector3f ppn() {
		return new Vector3f(maxX, maxY, minZ);
	}

	public Vector3f ppp() {
		return new Vector3f(maxX, maxY, maxZ);
	}

	public Vector3f npp() {
		return new Vector3f(minX, maxY, maxZ);
	}

	public AAFBB inflate(float xAdd, float yAdd, float zAdd) {
		return new AAFBB(minX - xAdd, minY - yAdd, minZ - zAdd, maxX + xAdd, maxY + yAdd, maxZ + zAdd);
	}

	public AAFBB inflate(float add) {
		return inflate(add, add, add);
	}

	public AAFBB intersect(AAFBB other) {
		return new AAFBB(
			Math.max(this.minX, other.minX),
			Math.max(this.minY, other.minY),
			Math.max(this.minZ, other.minZ),
			Math.min(this.maxX, other.maxX),
			Math.min(this.maxY, other.maxY),
			Math.min(this.maxZ, other.maxZ)
		);
	}

	public AAFBB minmax(AAFBB other) {
		return new AAFBB(
			Math.min(this.minX, other.minX),
			Math.min(this.minY, other.minY),
			Math.min(this.minZ, other.minZ),
			Math.max(this.maxX, other.maxX),
			Math.max(this.maxY, other.maxY),
			Math.max(this.maxZ, other.maxZ)
		);
	}

	public AABB move(float x, float y, float z) {
		return new AABB(
			minX + x,
			minY + y,
			minZ + z,
			maxX + x,
			maxY + y,
			maxZ + z
		);
	}

	public float distanceToSqr(float x, float y, float z) {
		float dx = Math.max(Math.max(minX - x, x - maxX), 0F);
		float dy = Math.max(Math.max(minY - y, y - maxY), 0F);
		float dz = Math.max(Math.max(minZ - z, z - maxZ), 0F);
		return Mth.lengthSquared(dx, dy, dz);
	}

	public float distanceToSqr(AAFBB other) {
		float dx = Math.max(Math.max(minX - other.maxX, other.minX - maxX), 0F);
		float dy = Math.max(Math.max(minY - other.maxY, other.minY - maxY), 0F);
		float dz = Math.max(Math.max(minZ - other.maxZ, other.minZ - maxZ), 0F);
		return Mth.lengthSquared(dx, dy, dz);
	}
}
