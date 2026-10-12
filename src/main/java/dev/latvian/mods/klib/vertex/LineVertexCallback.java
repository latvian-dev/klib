package dev.latvian.mods.klib.vertex;

import dev.latvian.mods.klib.math.Line3f;
import org.joml.Vector3fc;

@FunctionalInterface
public interface LineVertexCallback {
	void lineVertex(float x, float y, float z, float nx, float ny, float nz);

	default void line(float x1, float y1, float z1, float x2, float y2, float z2, float nx, float ny, float nz) {
		lineVertex(x1, y1, z1, nx, ny, nz);
		lineVertex(x2, y2, z2, nx, ny, nz);
	}

	default void line(float x1, float y1, float z1, float x2, float y2, float z2) {
		float nx = x2 - x1;
		float ny = y2 - y1;
		float nz = z2 - z1;

		if (nx != 0F && ny == 0F && nz == 0F) {
			line(x1, y1, z1, x2, y2, z2, nx > 0F ? 1F : -1F, 0F, 0F);
		} else if (nx == 0F && ny != 0F && nz == 0F) {
			line(x1, y1, z1, x2, y2, z2, 0F, ny > 0F ? 1F : -1F, 0F);
		} else if (nx == 0F && ny == 0F && nz != 0F) {
			line(x1, y1, z1, x2, y2, z2, 0F, 0F, nz > 0F ? 1F : -1F);
		} else {
			float lenSq = nx * nx + ny * ny + nz * nz;

			if (lenSq > 0F) {
				float len = org.joml.Math.sqrt(lenSq);
				line(x1, y1, z1, x2, y2, z2, nx / len, ny / len, nz / len);
			}
		}
	}

	default void line(Vector3fc start, Vector3fc end) {
		if (start != end) {
			line(start.x(), start.y(), start.z(), end.x(), end.y(), end.z());
		}
	}

	default void line(Line3f line) {
		line(line.start(), line.end());
	}

	default void cuboidLines(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		if (minY == maxY && minZ == maxZ) {
			line(minX, minY, minZ, maxX, minY, minZ, 1F, 0F, 0F);
		} else if (minX == maxX && minZ == maxZ) {
			line(minX, minY, minZ, minX, maxY, minZ, 0F, 1F, 0F);
		} else if (minX == maxX && minY == maxY) {
			line(minX, minY, minZ, minX, minY, maxZ, 0F, 0F, 1F);
		} else if (minX == maxX) {
			line(minX, minY, minZ, minX, maxY, minZ, 0F, 1F, 0F);
			line(minX, minY, minZ, minX, minY, maxZ, 0F, 0F, 1F);
			line(minX, maxY, minZ, minX, maxY, maxZ, 0F, 0F, 1F);
			line(minX, maxY, maxZ, minX, minY, maxZ, 0F, -1F, 0F);
		} else if (minY == maxY) {
			line(minX, minY, minZ, maxX, minY, minZ, 1F, 0F, 0F);
			line(minX, minY, minZ, minX, minY, maxZ, 0F, 0F, 1F);
			line(minX, minY, maxZ, maxX, minY, maxZ, 1F, 0F, 0F);
			line(maxX, minY, maxZ, maxX, minY, minZ, 0F, 0F, -1F);
		} else if (minZ == maxZ) {
			line(minX, minY, minZ, maxX, minY, minZ, 1F, 0F, 0F);
			line(minX, minY, minZ, minX, maxY, minZ, 0F, 1F, 0F);
			line(maxX, minY, minZ, maxX, maxY, minZ, 0F, 1F, 0F);
			line(maxX, maxY, minZ, minX, maxY, minZ, -1F, 0F, 0F);
		} else if (minX != maxX || minY != maxY || minZ != maxZ) {
			line(minX, minY, minZ, maxX, minY, minZ, 1F, 0F, 0F);
			line(minX, minY, minZ, minX, maxY, minZ, 0F, 1F, 0F);
			line(minX, minY, minZ, minX, minY, maxZ, 0F, 0F, 1F);
			line(maxX, minY, minZ, maxX, maxY, minZ, 0F, 1F, 0F);
			line(maxX, maxY, minZ, minX, maxY, minZ, -1F, 0F, 0F);
			line(minX, maxY, minZ, minX, maxY, maxZ, 0F, 0F, 1F);
			line(minX, maxY, maxZ, minX, minY, maxZ, 0F, -1F, 0F);
			line(minX, minY, maxZ, maxX, minY, maxZ, 1F, 0F, 0F);
			line(maxX, minY, maxZ, maxX, minY, minZ, 0F, 0F, -1F);
			line(minX, maxY, maxZ, maxX, maxY, maxZ, 1F, 0F, 0F);
			line(maxX, minY, maxZ, maxX, maxY, maxZ, 0F, 1F, 0F);
			line(maxX, maxY, minZ, maxX, maxY, maxZ, 0F, 0F, 1F);
		}
	}

	default void frameLines(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float cornerSize, float edgeSize) {
		float c = cornerSize * 0.5F;
		float e = edgeSize * 0.5F;
		float o = c * 2F;

		cuboidLines(minX - c, minY - c, minZ - c, minX + c, minY + c, minZ + c);
		cuboidLines(minX - c, minY - c, maxZ - c, minX + c, minY + c, maxZ + c);
		cuboidLines(minX - c, maxY - c, minZ - c, minX + c, maxY + c, minZ + c);
		cuboidLines(minX - c, maxY - c, maxZ - c, minX + c, maxY + c, maxZ + c);
		cuboidLines(maxX - c, minY - c, minZ - c, maxX + c, minY + c, minZ + c);
		cuboidLines(maxX - c, minY - c, maxZ - c, maxX + c, minY + c, maxZ + c);
		cuboidLines(maxX - c, maxY - c, minZ - c, maxX + c, maxY + c, minZ + c);
		cuboidLines(maxX - c, maxY - c, maxZ - c, maxX + c, maxY + c, maxZ + c);

		cuboidLines(minX - e, minY - e, minZ - c + o, minX + e, minY + e, maxZ + c - o);
		cuboidLines(maxX - e, minY - e, minZ - c + o, maxX + e, minY + e, maxZ + c - o);
		cuboidLines(minX - c + o, minY - e, minZ - e, maxX + c - o, minY + e, minZ + e);
		cuboidLines(minX - c + o, minY - e, maxZ - e, maxX + c - o, minY + e, maxZ + e);

		cuboidLines(minX - e, maxY - e, minZ - c + o, minX + e, maxY + e, maxZ + c - o);
		cuboidLines(maxX - e, maxY - e, minZ - c + o, maxX + e, maxY + e, maxZ + c - o);
		cuboidLines(minX - c + o, maxY - e, minZ - e, maxX + c - o, maxY + e, minZ + e);
		cuboidLines(minX - c + o, maxY - e, maxZ - e, maxX + c - o, maxY + e, maxZ + e);

		cuboidLines(minX - e, minY - c + o, minZ - e, minX + e, maxY + c - o, minZ + e);
		cuboidLines(maxX - e, minY - c + o, minZ - e, maxX + e, maxY + c - o, minZ + e);
		cuboidLines(minX - e, minY - c + o, maxZ - e, minX + e, maxY + c - o, maxZ + e);
		cuboidLines(maxX - e, minY - c + o, maxZ - e, maxX + e, maxY + c - o, maxZ + e);
	}

	default void circleLines(float size, int detail) {
		float r = Math.max(size / 2F, 0F);
		double rs = Math.PI * 2D / (double) detail;

		for (int i = 0; i < detail; i++) {
			float cx = (float) (Math.cos(i * rs) * r);
			float cz = (float) (Math.sin(i * rs) * r);
			float nx = (float) (Math.cos((i + 1D) * rs) * r);
			float nz = (float) (Math.sin((i + 1D) * rs) * r);

			line(cx, 0F, cz, nx, 0F, nz);
		}
	}

	default void cylinderLines(float width, float height, int detail, int hdetail) {
		float r = Math.max(width / 2F, 0F);
		float h = Math.max(height / 2F, 0F);
		double rs = Math.PI * 2D / (double) detail;

		for (int i = 0; i < detail; i++) {
			float cx = (float) (Math.cos(i * rs) * r);
			float cz = (float) (Math.sin(i * rs) * r);
			float nx = (float) (Math.cos((i + 1D) * rs) * r);
			float nz = (float) (Math.sin((i + 1D) * rs) * r);

			line(cx, h, cz, nx, h, nz);

			if (h > 0F) {
				line(cx, -h, cz, nx, -h, nz);
			}
		}

		if (h > 0F) {
			double rsv = Math.PI * 2D / (double) hdetail;

			for (int i = 0; i < hdetail; i++) {
				float cx = (float) (Math.cos(i * rsv) * r);
				float cz = (float) (Math.sin(i * rsv) * r);
				line(cx, -h, cz, cx, h, cz, 0F, 1F, 0F);
			}
		}
	}
}
