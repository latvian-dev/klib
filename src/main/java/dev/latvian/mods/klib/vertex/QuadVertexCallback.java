package dev.latvian.mods.klib.vertex;

@FunctionalInterface
public interface QuadVertexCallback {
	void quadVertex(float x, float y, float z, float u, float v, float nx, float ny, float nz);

	default void downQuad(float y, float minX, float minZ, float maxX, float maxZ) {
		if (minX == maxX || minZ == maxZ) {
			return;
		}

		quadVertex(minX, y, minZ, 0F, 0F, 0F, -1F, 0F);
		quadVertex(maxX, y, minZ, 1F, 0F, 0F, -1F, 0F);
		quadVertex(maxX, y, maxZ, 1F, 1F, 0F, -1F, 0F);
		quadVertex(minX, y, maxZ, 0F, 1F, 0F, -1F, 0F);
	}

	default void upQuad(float y, float minX, float minZ, float maxX, float maxZ) {
		if (minX == maxX || minZ == maxZ) {
			return;
		}

		quadVertex(minX, y, minZ, 0F, 0F, 0F, 1F, 0F);
		quadVertex(minX, y, maxZ, 0F, 1F, 0F, 1F, 0F);
		quadVertex(maxX, y, maxZ, 1F, 1F, 0F, 1F, 0F);
		quadVertex(maxX, y, minZ, 1F, 0F, 0F, 1F, 0F);
	}

	default void northQuad(float z, float minX, float minY, float maxX, float maxY) {
		if (minY == maxY || minX == maxX) {
			return;
		}

		quadVertex(minX, minY, z, 1F, 1F, 0F, 0F, -1F);
		quadVertex(minX, maxY, z, 1F, 0F, 0F, 0F, -1F);
		quadVertex(maxX, maxY, z, 0F, 0F, 0F, 0F, -1F);
		quadVertex(maxX, minY, z, 0F, 1F, 0F, 0F, -1F);
	}

	default void southQuad(float z, float minX, float minY, float maxX, float maxY) {
		if (minY == maxY || minX == maxX) {
			return;
		}

		quadVertex(maxX, minY, z, 1F, 1F, 0F, 0F, 1F);
		quadVertex(maxX, maxY, z, 1F, 0F, 0F, 0F, 1F);
		quadVertex(minX, maxY, z, 0F, 0F, 0F, 0F, 1F);
		quadVertex(minX, minY, z, 0F, 1F, 0F, 0F, 1F);
	}

	default void westQuad(float x, float minY, float minZ, float maxY, float maxZ) {
		if (minY == maxY || minZ == maxZ) {
			return;
		}

		quadVertex(x, minY, minZ, 0F, 1F, -1F, 0F, 0F);
		quadVertex(x, minY, maxZ, 1F, 1F, -1F, 0F, 0F);
		quadVertex(x, maxY, maxZ, 1F, 0F, -1F, 0F, 0F);
		quadVertex(x, maxY, minZ, 0F, 0F, -1F, 0F, 0F);
	}

	default void eastQuad(float x, float minY, float minZ, float maxY, float maxZ) {
		if (minY == maxY || minZ == maxZ) {
			return;
		}

		quadVertex(x, maxY, minZ, 1F, 0F, 1F, 0F, 0F);
		quadVertex(x, maxY, maxZ, 0F, 0F, 1F, 0F, 0F);
		quadVertex(x, minY, maxZ, 0F, 1F, 1F, 0F, 0F);
		quadVertex(x, minY, minZ, 1F, 1F, 1F, 0F, 0F);
	}

	default void cuboidQuads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		downQuad(minY, minX, minZ, maxX, maxZ);
		upQuad(maxY, minX, minZ, maxX, maxZ);
		northQuad(minZ, minX, minY, maxX, maxY);
		southQuad(maxZ, minX, minY, maxX, maxY);
		westQuad(minX, minY, minZ, maxY, maxZ);
		eastQuad(maxX, minY, minZ, maxY, maxZ);
	}

	default void frameQuads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float cornerSize, float edgeSize) {
		float c = cornerSize * 0.5F;
		float e = edgeSize * 0.5F;
		float o = c * 2F;

		cuboidQuads(minX - c, minY - c, minZ - c, minX + c, minY + c, minZ + c);
		cuboidQuads(minX - c, minY - c, maxZ - c, minX + c, minY + c, maxZ + c);
		cuboidQuads(minX - c, maxY - c, minZ - c, minX + c, maxY + c, minZ + c);
		cuboidQuads(minX - c, maxY - c, maxZ - c, minX + c, maxY + c, maxZ + c);
		cuboidQuads(maxX - c, minY - c, minZ - c, maxX + c, minY + c, minZ + c);
		cuboidQuads(maxX - c, minY - c, maxZ - c, maxX + c, minY + c, maxZ + c);
		cuboidQuads(maxX - c, maxY - c, minZ - c, maxX + c, maxY + c, minZ + c);
		cuboidQuads(maxX - c, maxY - c, maxZ - c, maxX + c, maxY + c, maxZ + c);

		cuboidQuads(minX - e, minY - e, minZ - c + o, minX + e, minY + e, maxZ + c - o);
		cuboidQuads(maxX - e, minY - e, minZ - c + o, maxX + e, minY + e, maxZ + c - o);
		cuboidQuads(minX - c + o, minY - e, minZ - e, maxX + c - o, minY + e, minZ + e);
		cuboidQuads(minX - c + o, minY - e, maxZ - e, maxX + c - o, minY + e, maxZ + e);

		cuboidQuads(minX - e, maxY - e, minZ - c + o, minX + e, maxY + e, maxZ + c - o);
		cuboidQuads(maxX - e, maxY - e, minZ - c + o, maxX + e, maxY + e, maxZ + c - o);
		cuboidQuads(minX - c + o, maxY - e, minZ - e, maxX + c - o, maxY + e, minZ + e);
		cuboidQuads(minX - c + o, maxY - e, maxZ - e, maxX + c - o, maxY + e, maxZ + e);

		cuboidQuads(minX - e, minY - c + o, minZ - e, minX + e, maxY + c - o, minZ + e);
		cuboidQuads(maxX - e, minY - c + o, minZ - e, maxX + e, maxY + c - o, minZ + e);
		cuboidQuads(minX - e, minY - c + o, maxZ - e, minX + e, maxY + c - o, maxZ + e);
		cuboidQuads(maxX - e, minY - c + o, maxZ - e, maxX + e, maxY + c - o, maxZ + e);
	}

	default void topCircleQuads(float size, int detail) {
		float r = Math.max(size / 2F, 0F);
		double rs = Math.PI * 2D / (double) detail;

		for (int i = 0; i < detail; i += 2) {
			float cx = (float) Math.cos(i * rs);
			float cz = (float) Math.sin(i * rs);
			float nx = (float) Math.cos((i + 1D) * rs);
			float nz = (float) Math.sin((i + 1D) * rs);
			float nnx = (float) Math.cos((i + 2D) * rs);
			float nnz = (float) Math.sin((i + 2D) * rs);

			quadVertex(0F, 0F, 0F, 0.5F, 0.5F, 0F, 1F, 0F);
			quadVertex(nnx * r, 0F, nnz * r, 0.5F + nnx / 2F, 0.5F + nnz / 2F, 0F, 1F, 0F);
			quadVertex(nx * r, 0F, nz * r, 0.5F + nx / 2F, 0.5F + nz / 2F, 0F, 1F, 0F);
			quadVertex(cx * r, 0F, cz * r, 0.5F + cx / 2F, 0.5F + cz / 2F, 0F, 1F, 0F);
		}
	}

	default void bottomCircleQuads(float size, int detail) {
		float r = Math.max(size / 2F, 0F);
		double rs = Math.PI * 2D / (double) detail;

		for (int i = 0; i < detail; i += 2) {
			float cx = (float) Math.cos(i * rs);
			float cz = (float) Math.sin(i * rs);
			float nx = (float) Math.cos((i + 1D) * rs);
			float nz = (float) Math.sin((i + 1D) * rs);
			float nnx = (float) Math.cos((i + 2D) * rs);
			float nnz = (float) Math.sin((i + 2D) * rs);

			quadVertex(0F, 0F, 0F, 0.5F, 0.5F, 0F, -1F, 0F);
			quadVertex(cx * r, 0F, cz * r, 0.5F + cx / 2F, 0.5F + cz / 2F, 0F, -1F, 0F);
			quadVertex(nx * r, 0F, nz * r, 0.5F + nx / 2F, 0.5F + nz / 2F, 0F, -1F, 0F);
			quadVertex(nnx * r, 0F, nnz * r, 0.5F + nnx / 2F, 0.5F + nnz / 2F, 0F, -1F, 0F);
		}
	}

	default void cylinderQuads(float width, float height, int detail) {
		float r = Math.max(width, 0F) / 2F;
		float h = Math.max(height, 0F) / 2F;
		double rs = Math.PI * 2D / (double) detail;

		for (int i = 0; i < detail; i += 2) {
			float cx = (float) Math.cos(i * rs);
			float cz = (float) Math.sin(i * rs);
			float nx = (float) Math.cos((i + 1D) * rs);
			float nz = (float) Math.sin((i + 1D) * rs);
			float nnx = (float) Math.cos((i + 2D) * rs);
			float nnz = (float) Math.sin((i + 2D) * rs);

			quadVertex(0F, h, 0F, 0.5F, 0.5F, 0F, 1F, 0F);
			quadVertex(nnx * r, h, nnz * r, 0.5F + nnx / 2F, 0.5F + nnz / 2F, 0F, 1F, 0F);
			quadVertex(nx * r, h, nz * r, 0.5F + nx / 2F, 0.5F + nz / 2F, 0F, 1F, 0F);
			quadVertex(cx * r, h, cz * r, 0.5F + cx / 2F, 0.5F + cz / 2F, 0F, 1F, 0F);

			if (h > 0F) {
				quadVertex(0F, -h, 0F, 0.5F, 0.5F, 0F, -1F, 0F);
				quadVertex(cx * r, -h, cz * r, 0.5F + cx / 2F, 0.5F + cz / 2F, 0F, -1F, 0F);
				quadVertex(nx * r, -h, nz * r, 0.5F + nx / 2F, 0.5F + nz / 2F, 0F, -1F, 0F);
				quadVertex(nnx * r, -h, nnz * r, 0.5F + nnx / 2F, 0.5F + nnz / 2F, 0F, -1F, 0F);

				float nrmx = (float) Math.cos((i + 0.5D) * rs);
				float nrmz = (float) Math.sin((i + 0.5D) * rs);
				float nnrmx = (float) Math.cos((i + 1.5D) * rs);
				float nnrmz = (float) Math.sin((i + 1.5D) * rs);
				float cu = i * 3F / (float) detail;
				float nu = (i + 1F) * 3F / (float) detail;
				float nnu = (i + 2F) * 3F / (float) detail;

				quadVertex(nx * r, h, nz * r, nu, 0F, nrmx, 0F, nrmz);
				quadVertex(nx * r, -h, nz * r, nu, 1F, nrmx, 0F, nrmz);
				quadVertex(cx * r, -h, cz * r, cu, 1F, nrmx, 0F, nrmz);
				quadVertex(cx * r, h, cz * r, cu, 0F, nrmx, 0F, nrmz);

				quadVertex(nnx * r, h, nnz * r, nnu, 0F, nnrmx, 0F, nnrmz);
				quadVertex(nnx * r, -h, nnz * r, nnu, 1F, nnrmx, 0F, nnrmz);
				quadVertex(nx * r, -h, nz * r, nu, 1F, nnrmx, 0F, nnrmz);
				quadVertex(nx * r, h, nz * r, nu, 0F, nnrmx, 0F, nnrmz);
			}
		}
	}
}
